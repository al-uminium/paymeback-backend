package paymeback.backend.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import paymeback.backend.domain.*;
import paymeback.backend.dto.request.ExpenseDTO;
import paymeback.backend.dto.request.ExpenseParticipantDTO;
import paymeback.backend.dto.mapper.ExpenseMapper;
import paymeback.backend.dto.response.ExpenseCreatedSummaryDTO;
import paymeback.backend.dto.response.ExpenseSummaryDTO;
import paymeback.backend.dto.response.MemberDebtDTO;
import paymeback.backend.dto.response.RecommendedSplitDTO;
import paymeback.backend.dto.response.projections.ExpenseProjection;
import paymeback.backend.dto.response.projections.ParticipantsProjection;
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



  public ExpenseCreatedSummaryDTO createAndSaveExpense(ExpenseDTO expenseDTO, UUID actorId) {
    // save expense first, need id which is generated after saving.
    Expense expense = mapper.expenseDtoToExpense(expenseDTO);
    expense = this.expenseRepository.save(expense);
    List<ExpenseParticipant> participants = saveToExpenseParticipants(expenseDTO.getParticipants(), expense.getId(), expenseDTO.getOwnerId());
    ExpenseCreatedSummaryDTO expenseCreatedSummaryDTO = new ExpenseCreatedSummaryDTO(expense, participants);
    this.auditLogService.createAndSaveAuditLog(expense.getGroupId(), actorId, EventType.EXPENSE_CREATED, "Expense was created.");

    return expenseCreatedSummaryDTO;
  }

  public ExpenseCreatedSummaryDTO updateExpense(ExpenseDTO expenseDTO, UUID expenseId, UUID actorId) {
    if (this.expenseRepository.existsById(expenseId)) {
      Expense expense = mapper.expenseDtoToExpense(expenseDTO);
      expense.setId(expenseId);
      // simplest way to deal with cases where instead of amount, they change the users who owe.
      this.expenseParticipantRepository.deleteAllByExpenseId(expenseId);

      List<ExpenseParticipant> participants = this.saveToExpenseParticipants(expenseDTO.getParticipants(), expenseId, expenseDTO.getOwnerId());
      ExpenseCreatedSummaryDTO expenseCreatedSummaryDTO = new ExpenseCreatedSummaryDTO(expense, participants);
      this.auditLogService.createAndSaveAuditLog(expense.getGroupId(), actorId, EventType.EXPENSE_EDITED, "Expense was edited.");
      return expenseCreatedSummaryDTO;
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

  public List<ExpenseSummaryDTO> getExpensesForGroup(UUID groupId) {
    List<ExpenseSummaryDTO> expenseSummaries = new ArrayList<>();
    List<ExpenseProjection> expenseProjections = this.expenseRepository.findAllExpensesByGroup(groupId);
    for (ExpenseProjection expense: expenseProjections) {
      List<ParticipantsProjection> participants = this.expenseParticipantRepository.findParticipantsForExpense(expense.getExpenseId());
      ExpenseSummaryDTO expenseSummaryDTO = new ExpenseSummaryDTO();
      expenseSummaryDTO.setExpense(expense);
      expenseSummaryDTO.setParticipants(participants);
      expenseSummaries.add(expenseSummaryDTO);
    }
    return expenseSummaries;
  }

  // show selected currency, add *all* when i setup redis for the conversion api
  public List<MemberDebtDTO> getMembersNetDebtByCurrency(UUID groupId, Currency currency) {
    List<Member> members = this.groupService.getMembers(groupId, true);
    List<MemberDebtDTO> memberDebtDTOs = new ArrayList<>();
    if (!members.isEmpty()) {
      for (Member member: members) {
        MemberDebtDTO memberDebtDTO = this.expenseParticipantRepository.calculateMemberNetDebtByCurrency(member.getId(), currency);
        memberDebtDTOs.add(memberDebtDTO);
      }
      return memberDebtDTOs;
    } else {
      return new ArrayList<>();
    }
  }

  //! This list expects an unsorted list of debts.
  //! The debts, when added all together, NEED to resolve to 0 (which should be the case if data is not compromised).
  //! Sorted list should look like: [5000, 2500, -3000, -4500]
  public List<RecommendedSplitDTO> calculateRecommendedSplit(List<MemberDebtDTO> memberDebtDTOs) {

    List<RecommendedSplitDTO> recommendedSplitDTOs = new ArrayList<>();

    int l_index = 0;
    int r_index = memberDebtDTOs.size() - 1;

    if (l_index == r_index || memberDebtDTOs.isEmpty()) {
      throw new IllegalArgumentException("Nothing to split.");
    }
    BigDecimal balance = memberDebtDTOs.stream()
        .reduce(BigDecimal.ZERO, (sum, debt) -> sum.add(debt.getNetDebt()), BigDecimal::add);
    if (balance.compareTo(BigDecimal.ZERO) != 0) {
      throw new IllegalArgumentException("Sum of net debt does not balance to 0, balance calculated: " + balance.toString());
    }
    memberDebtDTOs.sort((current, next) -> next.getNetDebt().compareTo(current.getNetDebt()));

    while (l_index < r_index) {
      MemberDebtDTO right = memberDebtDTOs.get(r_index);
      MemberDebtDTO left = memberDebtDTOs.get(l_index);
      if (right.getNetDebt().compareTo(BigDecimal.ZERO) == 0) {
        r_index--;
        continue;
      }
      if (left.getNetDebt().compareTo(BigDecimal.ZERO) == 0) {
        l_index++;
        continue;
      }

      RecommendedSplitDTO dto = new RecommendedSplitDTO(left.getMemberId(), left.getMemberName(), right.getMemberId(), right.getMemberName());
      // checking if sum of debts is positive -> if positive means left owes more than what right is owed.
      BigDecimal debtSum = left.getNetDebt().add(right.getNetDebt());
      if (debtSum.compareTo(BigDecimal.ZERO) >= 0) {
        dto.setShouldPayAmount(right.getNetDebt());
        left.setNetDebt(debtSum);
        right.setNetDebt(BigDecimal.ZERO);
      } else {
        dto.setShouldPayAmount(left.getNetDebt());
        right.setNetDebt(debtSum);
        left.setNetDebt(BigDecimal.ZERO);
      }
      recommendedSplitDTOs.add(dto);
    }
    return recommendedSplitDTOs;
  }
}
