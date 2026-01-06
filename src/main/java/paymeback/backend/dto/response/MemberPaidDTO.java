package paymeback.backend.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

@Data
public class MemberPaidDTO {
  private UUID memberId;
  private String memberName;
  private BigDecimal netPaid;

  public MemberPaidDTO(UUID memberId, String memberName, BigDecimal netPaid) {
    this.memberId = memberId;
    this.memberName = memberName;
    this.netPaid = netPaid;
  }
}
