package paymeback.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import paymeback.backend.domain.Expense;

@Repository
public interface ExpensesRepository extends JpaRepository<Expense, String> {
}
