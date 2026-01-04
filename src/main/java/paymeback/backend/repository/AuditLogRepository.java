package paymeback.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import paymeback.backend.domain.AuditLog;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}
