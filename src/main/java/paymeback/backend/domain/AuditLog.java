package paymeback.backend.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
public class AuditLog {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "log_id")
  private UUID id;

  @Column(name = "group_id")
  private UUID groupId;

  @Column(name = "actor_member_id")
  private UUID actorId;

  @Column(name = "event_type")
  @Enumerated(EnumType.STRING)
  private EventType eventType;

  @Column(name = "log_message")
  private String message;

  @CreationTimestamp
  @Column(name = "log_ts")
  private Instant createdTs;
}
