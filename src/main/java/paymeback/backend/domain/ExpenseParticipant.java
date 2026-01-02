package paymeback.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "expense_participant")
public class ExpenseParticipant {

  @EmbeddedId
  private ExpenseDetails id;

  @Column(name = "amount_owed")
  private BigDecimal amountOwed;
}
