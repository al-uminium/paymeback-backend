package paymeback.backend.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class RecommendedSplitDTO {
  private UUID payerId;
  private String payerName;
  private UUID payeeId;
  private String payeeName;
  private BigDecimal shouldPayAmount;

  public RecommendedSplitDTO(UUID payerId, String payerName, UUID payeeId, String payeeName) {
    this.payerId = payerId;
    this.payerName = payerName;
    this.payeeId = payeeId;
    this.payeeName = payeeName;
  }


}
