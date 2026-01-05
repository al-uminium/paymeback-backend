package paymeback.backend.service;

import org.springframework.stereotype.Service;
import paymeback.backend.domain.AuditLog;
import paymeback.backend.domain.EventType;
import paymeback.backend.dto.response.projections.AuditLogProjection;
import paymeback.backend.exception.GroupNotFoundException;
import paymeback.backend.repository.AuditLogRepository;
import paymeback.backend.repository.ExpenseGroupRepository;

import java.util.List;
import java.util.UUID;

@Service
public class AuditLogService {
  private final AuditLogRepository auditLogRepository;
  private final ExpenseGroupRepository groupRepository;

  public AuditLogService(AuditLogRepository auditLogRepository, ExpenseGroupRepository groupRepository) {
    this.auditLogRepository = auditLogRepository;
    this.groupRepository = groupRepository;
  }

  public void createAndSaveAuditLog(UUID groupId, UUID actorId, EventType eventType, String message) {
    AuditLog log = new AuditLog();

    log.setGroupId(groupId);
    log.setActorId(actorId);
    log.setEventType(eventType);
    log.setMessage(message);
    this.auditLogRepository.save(log);
  }

  public void saveAuditLog(AuditLog log) {
    this.auditLogRepository.save(log);
  }

  public List<AuditLogProjection> getGroupAuditLogs(UUID groupId) {
    if (groupRepository.existsById(groupId)) {
      return this.auditLogRepository.findAuditLogsByGroupId(groupId);
    } else {
      throw new GroupNotFoundException("Group of id " + groupId.toString() + " cannot be found");
    }
  }
}
