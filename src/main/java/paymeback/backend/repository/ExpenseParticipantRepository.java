package paymeback.backend.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;
import paymeback.backend.dto.response.MemberDebtDTO;
import paymeback.backend.domain.ExpenseDetails;
import paymeback.backend.domain.ExpenseParticipant;
import paymeback.backend.dto.response.projections.ParticipantsProjection;

import java.util.Currency;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseParticipantRepository extends JpaRepository<ExpenseParticipant, ExpenseDetails> {

  @Modifying
  @Transactional
  @NativeQuery("DELETE FROM expense_participant WHERE expense_id=?1")
  void deleteAllByExpenseId(UUID expenseId);

  @NativeQuery("""
      SELECT
      	ep.member_id AS id,
      	m.member_name AS name,
      	ep.amount_owed AS amount_owed,
      	ep.amount_owed < 0 AS is_payer
      FROM expense_participant ep
      JOIN member m
      	ON ep.member_id = m.member_id
      JOIN expense e
      	ON ep.expense_id = e.expense_id
      WHERE
      	ep.expense_id=?1;
      """)
  public List<ParticipantsProjection> findParticipantsForExpense(UUID expenseId);

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

  @NativeQuery("""
      SELECT
      	m.member_id AS member_id,
      	m.member_name AS member_name,
      	SUM(amount_owed) AS net_debt
      FROM expense_participant ep
      JOIN member m
      	ON ep.member_id = m.member_id
      JOIN expense e
      	ON e.expense_id = ep.expense_id
      WHERE ep.member_id=?1
      	AND e.expense_currency=?2
      	AND e.is_archived='false'
      GROUP BY m.member_id, m.member_name, e.expense_currency;
      """)
  MemberDebtDTO calculateMemberNetDebtByCurrency(UUID memberId, Currency currency);
}
