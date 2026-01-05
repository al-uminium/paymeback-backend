package paymeback.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paymeback.backend.domain.Expense;
import paymeback.backend.domain.Member;
import paymeback.backend.dto.request.CreateGroupAndMembersDTO;
import paymeback.backend.dto.request.MemberDTO;
import paymeback.backend.dto.request.MemberDTOs;
import paymeback.backend.dto.response.ExpenseSummaryDTO;
import paymeback.backend.dto.response.MemberDebtDTO;
import paymeback.backend.dto.response.GroupDetailsDTO;
import paymeback.backend.dto.response.RecommendedSplitDTO;
import paymeback.backend.dto.response.projections.AuditLogProjection;
import paymeback.backend.service.AuditLogService;
import paymeback.backend.service.ExpenseService;
import paymeback.backend.service.GroupManagementService;

import java.util.Currency;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/group")
public class ExpenseGroupController {

  private final GroupManagementService groupService;
  private final ExpenseService expenseService;
  private final AuditLogService auditLogService;

  public ExpenseGroupController(GroupManagementService groupService, ExpenseService expenseService, AuditLogService auditLogService) {
    this.groupService = groupService;
    this.expenseService = expenseService;
    this.auditLogService = auditLogService;
  }

  @PostMapping("/create")
  public ResponseEntity<GroupDetailsDTO> createGroup(@RequestBody CreateGroupAndMembersDTO createGroupAndMembersDTO) {
    GroupDetailsDTO response = this.groupService.createGroupAndMembers(createGroupAndMembersDTO);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  // note: {{base-url}}/api/group/:token?asdasd=true will give includeMembers=false, idk how to sanitize it properly :(
  @GetMapping("/{token:[a-zA-Z]{30}}")
  public ResponseEntity<GroupDetailsDTO> getGroupDetails(
      @PathVariable("token") String token,
      @RequestParam(name = "includeMembers", required = false) boolean includeMembers) {

    GroupDetailsDTO response = this.groupService.getGroupDetails(token, includeMembers);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/{groupId}/members")
  public ResponseEntity<List<Member>> getMembers(
      @PathVariable("groupId") UUID groupId,
      @RequestParam(name = "active", defaultValue = "true") boolean isActive) {
    List<Member> members = this.groupService.getMembers(groupId, isActive);

    return new ResponseEntity<>(members, HttpStatus.OK);
  }

  @PutMapping("/{groupId}/members")
  public ResponseEntity<List<Member>> addMembers(
      @PathVariable("groupId") UUID groupId,
      @RequestHeader("X-Actor-Id") UUID actorId,
      @RequestBody MemberDTOs memberDTOs) {
    List<Member> members = this.groupService.createMembers(memberDTOs.getMembers(), groupId, actorId);

    return new ResponseEntity<>(members, HttpStatus.OK);
  }

  @PutMapping("/member/{memberId}")
  public ResponseEntity<Member> updateMember(
      @PathVariable("memberId") UUID memberId,
      @RequestHeader("X-Group-Id") UUID groupId,
      @RequestHeader("X-Actor-Id") UUID actorId,
      @RequestParam(name = "delete", defaultValue = "false") boolean isSoftDelete,
      @RequestBody(required = false) MemberDTO memberDTO
  ) {
    Member member;
    if (isSoftDelete) {
      member = this.groupService.softDelete(memberId, groupId, actorId);
    } else {
      if (memberDTO == null) {
        throw new IllegalArgumentException("Member name is required for update");
      }
      member = this.groupService.updateMember(memberId, memberDTO, groupId, actorId);
    }
    return new ResponseEntity<>(member, HttpStatus.OK);
  }

  @GetMapping("/{groupId}/expenses")
  public ResponseEntity<List<ExpenseSummaryDTO>> getExpenses(@PathVariable(name = "groupId") UUID groupId) {
    List<ExpenseSummaryDTO> expenses = this.expenseService.getExpensesForGroup(groupId);
    return new ResponseEntity<>(expenses, HttpStatus.OK);
  }

  @GetMapping("/{groupId}/expenses/{currency}/debts")
  public ResponseEntity<List<MemberDebtDTO>> getExpenseDebtsByCurrency(
      @PathVariable(name = "groupId") UUID groupId,
      @PathVariable(name = "currency") Currency currency
  ) {
    List<MemberDebtDTO> memberDebtDTOs = this.expenseService.getMembersNetDebtByCurrency(groupId, currency);

    return new ResponseEntity<>(memberDebtDTOs, HttpStatus.OK);
  }

  @GetMapping("/{groupId}/logs")
  public ResponseEntity<List<AuditLogProjection>> getGroupAuditLogs(@PathVariable(name = "groupId") UUID groupId) {
    List<AuditLogProjection> logs = this.auditLogService.getGroupAuditLogs(groupId);

    return new ResponseEntity<>(logs, HttpStatus.OK);
  }

  @GetMapping("/{groupId}/expenses/{currency}/debts/split")
  public ResponseEntity<List<RecommendedSplitDTO>> getRecommendedSplit(
      @PathVariable(name = "groupId") UUID groupId,
      @PathVariable(name = "currency") Currency currency
  ) {
    List<MemberDebtDTO> memberDebtDTOs = this.expenseService.getMembersNetDebtByCurrency(groupId, currency);
    List<RecommendedSplitDTO> recommendedSplitDTOs = this.expenseService.calculateRecommendedSplit(memberDebtDTOs);

    return new ResponseEntity<>(recommendedSplitDTOs, HttpStatus.OK);
  }
}

