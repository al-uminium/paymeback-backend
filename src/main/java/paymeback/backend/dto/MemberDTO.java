package paymeback.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberDTO {
  @NotBlank
  private String name;
}
