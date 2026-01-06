package paymeback.backend.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

@Data
public class CreateSettlementDTO {
  private BigDecimal amountPaid;
  private Currency currency;
}
