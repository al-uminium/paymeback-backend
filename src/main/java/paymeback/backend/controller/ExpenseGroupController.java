package paymeback.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paymeback.backend.domain.ExpenseGroup;
import paymeback.backend.domain.Member;
import paymeback.backend.dto.CreateGroupAndMembersDTO;
import paymeback.backend.dto.MemberDTO;
import paymeback.backend.dto.MemberDTOs;
import paymeback.backend.dto.response.GroupDetailsResponse;
import paymeback.backend.service.GroupManagementService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/group")
public class ExpenseGroupController {

  private final GroupManagementService groupService;

  public ExpenseGroupController(GroupManagementService groupService) {
    this.groupService = groupService;
  }

  @PostMapping("/create")
  public ResponseEntity<GroupDetailsResponse> createGroup(@RequestBody CreateGroupAndMembersDTO createGroupAndMembersDTO) {
    GroupDetailsResponse response = this.groupService.createGroupAndMembers(createGroupAndMembersDTO);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  // note: {{base-url}}/api/group/:token?asdasd=true will give includeMembers=false, idk how to sanitize it properly :(
  @GetMapping("/{token:[a-zA-Z]{30}}")
  public ResponseEntity<GroupDetailsResponse> getGroupDetails(
      @PathVariable("token") String token,
      @RequestParam(name = "includeMembers", required = false) boolean includeMembers) {

    GroupDetailsResponse response = this.groupService.getGroupDetails(token, includeMembers);

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
      @RequestBody MemberDTOs memberDTOs) {
    List<Member> members = this.groupService.createMembers(memberDTOs.getMembers(), groupId);

    return new ResponseEntity<>(members, HttpStatus.OK);
  }

  @PutMapping("/member/{memberId}")
  public ResponseEntity<Member> updateMember(
      @PathVariable("memberId") UUID memberId,
      @RequestParam(name = "softDelete", defaultValue = "false") boolean isSoftDelete,
      @RequestBody(required = false) MemberDTO memberDTO
  ) {
    Member member;
    if (isSoftDelete) {
      member = this.groupService.softDelete(memberId);
    } else {
      if (memberDTO == null) {
        throw new IllegalArgumentException("Member name is required for update");
      }
      member = this.groupService.updateMember(memberId, memberDTO);
    }
    return new ResponseEntity<>(member, HttpStatus.OK);
  }
}
