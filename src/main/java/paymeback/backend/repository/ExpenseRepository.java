package paymeback.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;
import paymeback.backend.domain.Expense;
import paymeback.backend.dto.response.projections.ExpenseProjection;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
  List<Expense> findAllByGroupIdAndIsArchivedFalseOrderByCreatedTsDesc(UUID groupId);
  @NativeQuery("""
      SELECT
      	e.expense_id AS expense_id,
      	e.expense_name AS expense_name,
      	e.expense_owner_id AS owner_id,
      	m.member_name AS owner_name,
      	e.expense_total_cost AS total_cost,
      	e.expense_currency AS expense_currency,
      	e.expense_date AS "date",
      	e.expense_created_ts AS created_ts
      FROM expense e
      JOIN member m
      	ON e.expense_owner_id = m.member_id
      WHERE e.group_id=?1
      AND e.is_archived='false';
      """)
  List<ExpenseProjection> findAllExpensesByGroup(UUID groupId);
}
