package paymeback.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Data
public class CreateGroupAndMembersDTO {
  @NotBlank
  private String groupName;
  @NotNull
  @Pattern(regexp = "[A-Z]{3}")
  private String defaultCurrency;
  @Valid
  private List<MemberDTO> members;
}
