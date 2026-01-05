package paymeback.backend.dto.response.projections;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Currency;
import java.util.UUID;


public interface ExpenseProjection {
  UUID getExpenseId();
  String getExpenseName();
  UUID getOwnerId();
  String getOwnerName();
  BigDecimal getTotalCost();
  Currency getExpenseCurrency();
  LocalDate getDate();
  Instant getCreatedTs();
}
