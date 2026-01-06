package paymeback.backend.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Currency;
import java.util.UUID;

@Data
@Entity
@Table(name = "expense")
public class Expense {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "expense_id")
  private UUID id;

  @Column(name = "group_id")
  private UUID groupId;

  @Column(name = "expense_owner_id")
  private UUID ownerId;

  @Column(name = "expense_name")
  private String name;

  @Column(name = "expense_total_cost")
  private BigDecimal totalCost;

  @Column(name = "expense_currency", nullable = false, length = 3)
  private Currency currency;

  @Column(name = "expense_date")
  private LocalDate createdDate; // when the expense was made

  @CreationTimestamp
  @Column(name = "expense_created_ts")
  private Instant createdTs; // when the expense was stored in DB

  @Column(name = "is_archived")
  private boolean isArchived = false;

  @Column(name = "archived_ts")
  private Instant archivedTs;
}
