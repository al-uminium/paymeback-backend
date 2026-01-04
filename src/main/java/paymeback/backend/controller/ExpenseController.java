package paymeback.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paymeback.backend.dto.ExpenseDTO;
import paymeback.backend.dto.response.ExpenseResponse;
import paymeback.backend.service.ExpenseService;

import java.util.UUID;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {

  private final ExpenseService expenseService;

  public ExpenseController(ExpenseService expenseService) {
    this.expenseService = expenseService;
  }

  @PostMapping("/create")
  public ResponseEntity<ExpenseResponse> createExpense(
      @RequestHeader("X-Actor-Id") UUID actorId,
      @RequestBody ExpenseDTO expenseDTO
  ) {
    ExpenseResponse expenseResponse = this.expenseService.createAndSaveExpense(expenseDTO, actorId);

    return new ResponseEntity<>(expenseResponse, HttpStatus.OK);
  }

  @DeleteMapping("/{expenseId}")
  public ResponseEntity<?> deleteExpense(
      @PathVariable(name = "expenseId") UUID expenseId,
      @RequestHeader("X-Group-Id") UUID groupId,
      @RequestHeader("X-Actor-Id") UUID actorId
  ) {
    this.expenseService.deleteExpense(expenseId, groupId, actorId);
    return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
  }

  @PostMapping("/{expenseId}")
  public ResponseEntity<ExpenseResponse> updateExpense(
      @RequestHeader("X-Actor-Id") UUID actorId,
      @PathVariable(name = "expenseId") UUID expenseId,
      @RequestBody ExpenseDTO expenseDTO
  ) {
    ExpenseResponse expenseResponse = this.expenseService.updateExpense(expenseDTO, expenseId, actorId);

    return new ResponseEntity<>(expenseResponse, HttpStatus.OK);
  }

}
