package paymeback.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Validated
@Data
public class CreateExpenseDTO {
  @NotBlank(message = "Expense name cannot be blank")
  private String expenseName;

  @NotNull
  private UUID ownerId;

  @Positive
  @NotNull(message = "Cost must not be null")
  private BigDecimal totalCost;

  @NotNull
  @Pattern(regexp = "[A-Z]{3}")
  private String currency;

  @PastOrPresent
  private LocalDate date;
}
