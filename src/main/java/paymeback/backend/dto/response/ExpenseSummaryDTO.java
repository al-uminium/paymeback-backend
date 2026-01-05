package paymeback.backend.dto.response;

import lombok.Data;
import paymeback.backend.dto.response.projections.ExpenseProjection;
import paymeback.backend.dto.response.projections.ParticipantsProjection;

import java.util.List;

@Data
public class ExpenseSummaryDTO {
  private ExpenseProjection expense;
  private List<ParticipantsProjection> participants;
}

