package paymeback.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExpenseParticipantDTOs {
  private List<ExpenseParticipantDTO> participants;
}
