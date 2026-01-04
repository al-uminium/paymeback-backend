package paymeback.backend.dto.response;

import lombok.Data;
import paymeback.backend.domain.ExpenseGroup;
import paymeback.backend.domain.Member;

import java.util.List;

@Data
public class GroupDetailsResponse {
  private ExpenseGroup groupDetails;
  private List<Member> members;
}
