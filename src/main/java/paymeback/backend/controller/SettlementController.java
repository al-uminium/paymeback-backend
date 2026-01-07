package paymeback.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paymeback.backend.domain.Settlement;
import paymeback.backend.dto.request.CreateSettlementDTO;
import paymeback.backend.dto.response.MemberDebtDTO;
import paymeback.backend.service.ExpenseService;
import paymeback.backend.service.SettlementService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/settlement")
public class SettlementController {

  private final SettlementService settlementService;
  private final ExpenseService expenseService;

  public SettlementController(SettlementService settlementService, ExpenseService expenseService) {
    this.settlementService = settlementService;
    this.expenseService = expenseService;
  }

  @PostMapping
  public ResponseEntity<Settlement> createSettlement(
      @RequestHeader("X-Payer-Id") UUID payerId,
      @RequestHeader("X-Payee-Id") UUID payeeId,
      @RequestHeader("X-Group-Id") UUID groupId,
      @RequestBody CreateSettlementDTO settlementDTO
      ) {

    Settlement createdSettlement = this.settlementService.createAndSaveSettlement(groupId, payerId, payeeId, settlementDTO);

    return new ResponseEntity<>(createdSettlement, HttpStatus.OK);
  }
}
