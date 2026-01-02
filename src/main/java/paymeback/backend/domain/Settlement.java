package paymeback.backend.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

@Entity
@Table(name = "settlement")
public class Settlement {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "settlement_id")
  private UUID id;

  @Column(name = "group_id")
  private UUID groupId;

  @Column(name = "payer_member_id")
  private UUID payerId;

  @Column(name = "payee_member_id")
  private UUID payeeId;

  @Column(name = "amount_paid")
  private BigDecimal amountPaid;

  @Column(name = "currency", length = 3)
  private Currency currency;

  @CreationTimestamp
  @Column(name = "settlement_ts")
  private Instant createdTs;

}
