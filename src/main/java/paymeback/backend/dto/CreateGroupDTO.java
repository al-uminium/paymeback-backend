package paymeback.backend.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Data
public class CreateGroupDTO {
  private String groupName;
  private String defaultCurrency;
  private List<MemberDTO> members;
}
