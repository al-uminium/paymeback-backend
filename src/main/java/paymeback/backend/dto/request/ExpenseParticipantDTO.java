package paymeback.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.UUID;

@Validated
@Data
public class ExpenseParticipantDTO {
  @NotBlank
  private UUID participantId;

  @Positive
  private BigDecimal amountOwed;
}
