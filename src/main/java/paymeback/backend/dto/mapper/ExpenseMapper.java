package paymeback.backend.dto.mapper;

import org.springframework.stereotype.Service;
import paymeback.backend.domain.Expense;
import paymeback.backend.domain.ExpenseDetails;
import paymeback.backend.domain.ExpenseParticipant;
import paymeback.backend.dto.ExpenseDTO;
import paymeback.backend.dto.ExpenseParticipantDTO;
import paymeback.backend.dto.response.ExpenseResponse;

import java.util.Currency;
import java.util.List;
import java.util.UUID;

@Service
public class ExpenseMapper {

  public Expense expenseDtoToExpense(ExpenseDTO expenseDTO) {
    Expense expense = new Expense();
    expense.setGroupId(expenseDTO.getGroupId());
    expense.setOwnerId(expenseDTO.getOwnerId());
    expense.setName(expenseDTO.getExpenseName());
    expense.setTotalCost(expenseDTO.getTotalCost());
    expense.setCurrency(Currency.getInstance(expenseDTO.getCurrency()));
    expense.setCreatedDate(expenseDTO.getDate());

    return expense;
  }

  public List<ExpenseParticipant> participantDtoToExpenseParticipants(List<ExpenseParticipantDTO> dto, UUID expenseId) {
    return dto.stream().map( participant -> {
      ExpenseDetails expenseDetails = new ExpenseDetails();
      expenseDetails.setExpenseId(expenseId);
      expenseDetails.setMemberId(participant.getParticipantId());
      ExpenseParticipant expenseParticipant = new ExpenseParticipant();
      expenseParticipant.setId(expenseDetails);
      expenseParticipant.setAmountOwed(participant.getAmountOwed());
      return expenseParticipant;
    }).toList();
  }

}
