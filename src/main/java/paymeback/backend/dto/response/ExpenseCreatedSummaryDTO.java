package paymeback.backend.dto.response;

import lombok.Data;
import paymeback.backend.domain.Expense;
import paymeback.backend.domain.ExpenseParticipant;

import java.util.List;

@Data
public class ExpenseCreatedSummaryDTO {
  private Expense expense;
  private List<ExpenseParticipant> participants;

  public ExpenseCreatedSummaryDTO() {
  }

  public ExpenseCreatedSummaryDTO(Expense expense, List<ExpenseParticipant> participants) {
    this.expense = expense;
    this.participants = participants;
  }
}
