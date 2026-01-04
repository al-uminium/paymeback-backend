package paymeback.backend.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;
import paymeback.backend.domain.ExpenseDetails;
import paymeback.backend.domain.ExpenseParticipant;

import java.util.UUID;

@Repository
public interface ExpenseParticipantRepository extends JpaRepository<ExpenseParticipant, ExpenseDetails> {
  @Modifying
  @Transactional
  @NativeQuery("DELETE FROM expense_participant WHERE expense_id=?1")
  void deleteAllByExpenseId(UUID expenseId);
}
