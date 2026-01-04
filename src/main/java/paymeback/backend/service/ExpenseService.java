package paymeback.backend.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import paymeback.backend.domain.*;
import paymeback.backend.dto.ExpenseDTO;
import paymeback.backend.dto.ExpenseParticipantDTO;
import paymeback.backend.dto.mapper.ExpenseMapper;
import paymeback.backend.dto.response.ExpenseResponse;
import paymeback.backend.dto.MemberDebtDTO;
import paymeback.backend.exception.ExpenseNotFoundException;
import paymeback.backend.exception.MemberNotFoundException;
import paymeback.backend.repository.ExpenseParticipantRepository;
import paymeback.backend.repository.ExpenseRepository;
import paymeback.backend.repository.MemberRepository;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
public class ExpenseService {

  private final ExpenseRepository expenseRepository;

  private final MemberRepository memberRepository;

  private final ExpenseParticipantRepository expenseParticipantRepository;

  private final GroupManagementService groupService;

  private final AuditLogService auditLogService;

  private final ExpenseMapper mapper;

  public ExpenseService(
      ExpenseRepository expenseRepository,
      MemberRepository memberRepository,
      ExpenseParticipantRepository expenseParticipantRepository,
      GroupManagementService groupService,
      AuditLogService auditLogService,
      ExpenseMapper mapper
  ) {
    this.expenseRepository = expenseRepository;
    this.memberRepository = memberRepository;
    this.expenseParticipantRepository = expenseParticipantRepository;
    this.groupService = groupService;
    this.auditLogService = auditLogService;
    this.mapper = mapper;
  }

  private BigDecimal calculateAmountPaid(List<ExpenseParticipantDTO> participants) {
    return participants.stream()
        .reduce(BigDecimal.ZERO, (sum, participant) -> sum.add(participant.getAmountOwed()), BigDecimal::add);
  }

  private List<ExpenseParticipant> saveToExpenseParticipants(List<ExpenseParticipantDTO> participantDTOs, UUID expenseId, UUID ownerId) {
    ExpenseParticipantDTO ownerDTO = new ExpenseParticipantDTO();
    ownerDTO.setParticipantId(ownerId);
    ownerDTO.setAmountOwed(this.calculateAmountPaid(participantDTOs).negate());
    participantDTOs.add(ownerDTO);

    List<ExpenseParticipant> participants = mapper.participantDtoToExpenseParticipants(participantDTOs, expenseId);
    List<ExpenseParticipant> savedParticipants = new ArrayList<>();
    for (ExpenseParticipant participant: participants) {
      if (this.memberRepository.existsById(participant.getId().getMemberId())){
        ExpenseParticipant savedParticipant = this.expenseParticipantRepository.save(participant);
        savedParticipants.add(savedParticipant);
      } else {
        throw new MemberNotFoundException("Member with id " + participant.getId().getMemberId() + "could not be found. Unable to add expense");
      }
    }
    return savedParticipants;
  }


  public ExpenseResponse createAndSaveExpense(ExpenseDTO expenseDTO, UUID actorId) {
    // save expense first, need id which is generated after saving.
    Expense expense = mapper.expenseDtoToExpense(expenseDTO);
    expense = this.expenseRepository.save(expense);
    List<ExpenseParticipant> participants = saveToExpenseParticipants(expenseDTO.getParticipants(), expense.getId(), expenseDTO.getOwnerId());
    ExpenseResponse expenseResponse = new ExpenseResponse(expense, participants);
    this.auditLogService.createAndSaveAuditLog(expense.getGroupId(), actorId, EventType.EXPENSE_CREATED, "Expense was created.");

    return expenseResponse;
  }

  public ExpenseResponse updateExpense(ExpenseDTO expenseDTO, UUID expenseId, UUID actorId) {
    if (this.expenseRepository.existsById(expenseId)) {
      Expense expense = mapper.expenseDtoToExpense(expenseDTO);
      expense.setId(expenseId);
      // simplest way to deal with cases where instead of amount, they change the users who owe.
      this.expenseParticipantRepository.deleteAllByExpenseId(expenseId);

      List<ExpenseParticipant> participants = this.saveToExpenseParticipants(expenseDTO.getParticipants(), expenseId, expenseDTO.getOwnerId());
      ExpenseResponse expenseResponse = new ExpenseResponse(expense, participants);
      this.auditLogService.createAndSaveAuditLog(expense.getGroupId(), actorId, EventType.EXPENSE_EDITED, "Expense was edited.");
      return expenseResponse;
    } else {
      throw new ExpenseNotFoundException("The expense of id " + expenseId.toString() + " you are trying to update does not exist.");
    }
  }

  public void deleteExpense(UUID expenseId, UUID groupId, UUID actorId) {
    if (this.expenseRepository.existsById(expenseId)) {
      this.expenseRepository.deleteById(expenseId); // no need to delete expense participants, it's written to delete on cascade.
      this.auditLogService.createAndSaveAuditLog(groupId, actorId, EventType.EXPENSE_DELETED, "Expense was deleted.");
    } else {
      throw new ExpenseNotFoundException("The expense of id " + expenseId.toString() + " you are trying to delete does not exist.");
    }
  }

  public List<MemberDebtDTO> getMembersNetDebt(UUID groupId) {
    List<Member> members = this.groupService.getMembers(groupId, true);
    List<MemberDebtDTO> memberDebtDTOs = new ArrayList<>();
    if (!members.isEmpty()) {
      for (Member member: members) {
        MemberDebtDTO memberDebtDTO = this.expenseParticipantRepository.calculateMemberNetDebt(member.getId());
        memberDebtDTOs.add(memberDebtDTO);
      }
      return memberDebtDTOs;
    } else {
      return new ArrayList<>();
    }
  }
}
