package paymeback.backend.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

@Data
@Entity
@Table(name = "expense_group")
public class ExpenseGroup {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "group_id")
  private UUID id;

  @Column(name = "group_name")
  private String name;

  @Column(name = "group_link_token")
  private String linkToken;

  @Column(name = "group_default_currency")
  private Currency defaultCurrency;

  @CreationTimestamp
  @Column(name = "group_created_ts")
  private Instant createdTs;

  @CreationTimestamp
  @Column(name = "group_last_activity_ts")
  private Instant lastActivityTs;

  @Column(name = "group_expiry_ts")
  private Instant expiryTs;
}
