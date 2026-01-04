package paymeback.backend.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;
import paymeback.backend.dto.MemberDebtDTO;
import paymeback.backend.domain.ExpenseDetails;
import paymeback.backend.domain.ExpenseParticipant;

import java.util.UUID;

@Repository
public interface ExpenseParticipantRepository extends JpaRepository<ExpenseParticipant, ExpenseDetails> {
  @Modifying
  @Transactional
  @NativeQuery("DELETE FROM expense_participant WHERE expense_id=?1")
  void deleteAllByExpenseId(UUID expenseId);

  @NativeQuery("""
      SELECT
      	m.member_id AS member_id,
      	m.member_name AS member_name,
      	SUM(amount_owed) AS net_debt
      FROM expense_participant ep
      JOIN member m
      	ON ep.member_id = m.member_id
      WHERE ep.member_id=?1
      GROUP BY m.member_id;
      """)
  MemberDebtDTO calculateMemberNetDebt(UUID memberId);
}
