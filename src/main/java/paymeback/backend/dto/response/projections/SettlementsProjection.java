package paymeback.backend.dto.response.projections;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

public interface SettlementsProjection {
  UUID getSettlementId();
  UUID payerId();
  String payerName();
  UUID payeeId();
  String payeeName();
  BigDecimal amountPaid();
  Currency currency();
  Instant createdTs();
}
