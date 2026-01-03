package paymeback.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import paymeback.backend.domain.ExpenseGroup;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpenseGroupRepository extends JpaRepository<ExpenseGroup, UUID> {
  Optional<ExpenseGroup> findByLinkToken(String token);
}
