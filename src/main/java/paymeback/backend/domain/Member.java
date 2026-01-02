package paymeback.backend.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "member")
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "member_id", nullable = false)
  private UUID id;

  @Column(name = "group_id")
  private UUID groupId;

  @Column(name = "member_name")
  private String name;

  @Column(name = "member_status")
  private String status;

  @Column(name = "member_created_ts")
  private Instant createdTs;

  @Column(name = "member_removed_ts")
  private Instant removedTs;
}
