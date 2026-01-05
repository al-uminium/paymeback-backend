package paymeback.backend.dto.response.projections;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

public interface ParticipantsProjection {
  String getName();
  UUID getId();
  BigDecimal getAmountOwed();
  Boolean getIsPayer();
}
