package paymeback.backend.dto.response.projections;

import paymeback.backend.domain.EventType;

import java.time.Instant;
import java.util.UUID;

public interface AuditLogProjection {
  UUID getLogId();
  UUID getActorId();
  String getActorName();
  EventType getEventType();
  String getLogMessage();
  Instant getLogTs();
}
