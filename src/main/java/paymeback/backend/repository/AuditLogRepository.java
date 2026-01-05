package paymeback.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import paymeback.backend.domain.AuditLog;
import paymeback.backend.dto.response.projections.AuditLogProjection;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
  @NativeQuery("""
      SELECT
      	l.log_id AS log_id,
      	l.actor_member_id AS actor_id,
      	m.member_name AS actor_name,
      	l.event_type AS event_type,
      	l.log_message AS log_message,
      	l.log_ts AS log_ts
      FROM audit_log l
      JOIN member m
      	ON l.actor_member_id = m.member_id
      WHERE l.group_id = ?1;
      """)
  List<AuditLogProjection> findAuditLogsByGroupId(UUID groupId);
}
