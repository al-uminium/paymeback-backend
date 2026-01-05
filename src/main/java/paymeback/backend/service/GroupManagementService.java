package paymeback.backend.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import paymeback.backend.domain.AuditLog;
import paymeback.backend.domain.EventType;
import paymeback.backend.domain.ExpenseGroup;
import paymeback.backend.domain.Member;
import paymeback.backend.dto.request.CreateGroupAndMembersDTO;
import paymeback.backend.dto.request.MemberDTO;
import paymeback.backend.dto.response.GroupDetailsDTO;
import paymeback.backend.exception.CannotDeleteWithActiveDebtException;
import paymeback.backend.exception.GroupNotFoundException;
import paymeback.backend.exception.MemberNotFoundException;
import paymeback.backend.repository.ExpenseGroupRepository;
import paymeback.backend.repository.ExpenseParticipantRepository;
import paymeback.backend.repository.MemberRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@Transactional
public class GroupManagementService {

  private final ExpenseGroupRepository expenseGroupRepository;

  private final MemberRepository memberRepository;

  private final ExpenseParticipantRepository expenseParticipantRepository;

  private final UtilService utilService;

  private final AuditLogService auditLogService;

  private final Logger logger = Logger.getAnonymousLogger();

  public GroupManagementService(
      ExpenseGroupRepository expenseGroupRepository,
      MemberRepository memberRepository,
      ExpenseParticipantRepository expenseParticipantRepository,
      UtilService utilService,
      AuditLogService auditLogService) {
    this.expenseGroupRepository = expenseGroupRepository;
    this.memberRepository = memberRepository;
    this.expenseParticipantRepository = expenseParticipantRepository;
    this.utilService = utilService;
    this.auditLogService = auditLogService;
  }

  private ExpenseGroup createGroup(CreateGroupAndMembersDTO groupDTO){
    ExpenseGroup expenseGroup = new ExpenseGroup();
    expenseGroup.setName(groupDTO.getGroupName());
    expenseGroup.setLinkToken(utilService.generateLinkToken(30));
    expenseGroup.setExpiryTs(utilService.generateExpiryDate());
    try {
      expenseGroup.setDefaultCurrency(Currency.getInstance(groupDTO.getDefaultCurrency()));
      expenseGroupRepository.save(expenseGroup);
    } catch (IllegalArgumentException ex) {
      logger.log(Level.WARNING, "Exception occurred", ex);
      throw new IllegalArgumentException(ex.getMessage());
    }
    return expenseGroup;
  }

  public List<Member> createMembers(List<MemberDTO> memberDTOs, UUID groupId, UUID actorId) {
    List<Member> members = new ArrayList<>();
    try {
      for (MemberDTO dto: memberDTOs) {
        Member member = new Member();
        member.setName(dto.getName());
        member.setGroupId(groupId);
        memberRepository.save(member);
        if (actorId != null) {
          this.auditLogService.createAndSaveAuditLog(groupId, actorId, EventType.MEMBER_ADDED, member.getName() + " was added to the group.");
        }
        members.add(member);
      }
    } catch (Exception ex) {
      logger.log(Level.WARNING, "Exception occurred", ex);
      throw new Error("Unable to save");
    }
    return members;
  }

  // manually created audit log to solve chicken and egg case between audit log, group and member creation
  public GroupDetailsDTO createGroupAndMembers(CreateGroupAndMembersDTO createGroupAndMembersDTO) {
    AuditLog log = new AuditLog();
    log.setCreatedTs(Instant.now());

    ExpenseGroup group = this.createGroup(createGroupAndMembersDTO);
    List<Member> members = this.createMembers(createGroupAndMembersDTO.getMembers(), group.getId(), null);
    Member creator = this.getCreator(createGroupAndMembersDTO.getCreator(), members);
    if (creator == null) {
      throw new IllegalArgumentException("A user must be selected to create a group.");
    } else {
      log.setGroupId(group.getId());
      log.setActorId(creator.getId());
      log.setEventType(EventType.GROUP_CREATED);
      log.setMessage(group.getName().concat(" was created."));
      this.auditLogService.saveAuditLog(log);
      for(Member member: members) {
        this.auditLogService.createAndSaveAuditLog(group.getId(), creator.getId(), EventType.MEMBER_ADDED, member.getName() + " was added to the group.");
      }
    }

    GroupDetailsDTO response = new GroupDetailsDTO();
    response.setGroupDetails(group);
    response.setMembers(members);
    return response;
  }

  public ExpenseGroup getExpenseGroupWithToken(String token) {
    Optional<ExpenseGroup> optExpenseGroup = expenseGroupRepository.findByLinkToken(token);
    if (optExpenseGroup.isEmpty()) {
      throw new GroupNotFoundException("Group not found");
    } else {
      return optExpenseGroup.get();
    }
  }

  public List<Member> getMembers(UUID groupId, boolean activeMembersOnly) {
    List<Member> members;
    if (expenseGroupRepository.existsById(groupId)) {
      if (activeMembersOnly) {
        members = memberRepository.findAllByGroupIdAndRemovedTsIsNull(groupId);
      } else {
        members = memberRepository.findAllByGroupId(groupId);
      }
      return members;
    } else {
      throw new GroupNotFoundException("Group not found for id: ".concat(groupId.toString()));
    }
  }

  public GroupDetailsDTO getGroupDetails(String token, boolean includeMembers) {
    GroupDetailsDTO response = new GroupDetailsDTO();
    ExpenseGroup group = this.getExpenseGroupWithToken(token);
    response.setGroupDetails(group);
    if (includeMembers) {
      response.setMembers(this.getMembers(group.getId(), true));
    }

    return response;
  }

  public Member softDelete(UUID memberId, UUID groupId, UUID actorId) {
    Optional<Member> optMember = this.memberRepository.findById(memberId);
    if (optMember.isEmpty()) {
      throw new MemberNotFoundException("Cannot find member with id: ".concat(memberId.toString()));
    } else {
      Member member = optMember.get();
      // check is member has debt
      BigDecimal debt = this.expenseParticipantRepository.calculateMemberNetDebt(memberId).getNetDebt();
      if (debt.compareTo(BigDecimal.ZERO) == 0) {
        member.setRemovedTs(Instant.now());
        member.setStatus("INACTIVE");
        this.memberRepository.save(member);
        this.auditLogService.createAndSaveAuditLog(groupId, actorId, EventType.MEMBER_REMOVED, member.getName() + " was removed from the group.");
        return member;
      } else {
        throw new CannotDeleteWithActiveDebtException("Member still has active debt, unable to delete");
      }
    }
  }

  // decided not to merge soft delete and update so it's clearer what is being done.
  public Member updateMember(UUID memberId, MemberDTO memberDTO, UUID groupId, UUID actorId) {
    Optional<Member> optMember = this.memberRepository.findById(memberId);
    if (optMember.isEmpty()) {
      throw new MemberNotFoundException("Cannot find member with id: ".concat(memberId.toString()));
    } else {
      Member member = optMember.get();
      String previousName = member.getName();
      member.setName(memberDTO.getName());
      this.memberRepository.save(member);
      this.auditLogService.createAndSaveAuditLog(groupId, actorId, EventType.MEMBER_EDITED, previousName + " changed their name to " + member.getName());
      return member;
    }
  }

  private Member getCreator(MemberDTO creatorDTO, List<Member> createdMembers) {
    for (Member member: createdMembers) {
      if (creatorDTO.getName().contentEquals(member.getName())) {
        return member;
      }
    }
    return null;
  }
}
