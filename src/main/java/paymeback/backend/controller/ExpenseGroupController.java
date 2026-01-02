package paymeback.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paymeback.backend.dto.CreateGroupDTO;
import paymeback.backend.service.GroupManagementService;

@RestController
@RequestMapping("/api/groups")
public class ExpenseGroupController {

  private final GroupManagementService groupService;

  public ExpenseGroupController(GroupManagementService groupService) {
    this.groupService = groupService;
  }

  @PostMapping("/create")
  public ResponseEntity<String> createGroup(@RequestBody CreateGroupDTO createGroupDTO) {
    String response = this.groupService.createGroup(createGroupDTO);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
