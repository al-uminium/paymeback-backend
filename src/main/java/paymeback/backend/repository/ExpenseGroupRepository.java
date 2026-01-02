package paymeback.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import paymeback.backend.domain.ExpenseGroup;

@Repository
public interface ExpenseGroupRepository extends JpaRepository<ExpenseGroup, String> {

}
