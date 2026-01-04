package paymeback.backend.dto.response;

import lombok.Data;
import paymeback.backend.domain.Expense;
import paymeback.backend.domain.ExpenseParticipant;

import java.util.List;

@Data
public class ExpenseResponse {
  private Expense expense;
  private List<ExpenseParticipant> participants;

  public ExpenseResponse() {
  }

  public ExpenseResponse(Expense expense, List<ExpenseParticipant> participants) {
    this.expense = expense;
    this.participants = participants;
  }
}
