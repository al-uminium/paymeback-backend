package paymeback.backend.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.UUID;

@Data
@Embeddable
public class ExpenseDetails {
  private UUID expenseId;
  private UUID memberId;
}
