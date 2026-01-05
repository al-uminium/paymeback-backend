package paymeback.backend.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class MemberDebtDTO {
  private UUID memberId;
  private String memberName;
  private BigDecimal netDebt;

  public MemberDebtDTO(UUID memberId, String memberName, BigDecimal netDebt) {
    this.memberId = memberId;
    this.memberName = memberName;
    this.netDebt = netDebt;
  }
}
