package paymeback.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;
import paymeback.backend.domain.Settlement;
import paymeback.backend.dto.response.MemberPaidDTO;

import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, UUID> {
  @NativeQuery("""
      SELECT
      	s.payer_member_id AS member_id,
      	m.member_name AS member_name,
      	SUM(amount_paid) AS net_paid
      FROM settlement s
      JOIN member m
      	ON s.payer_member_id = m.member_id
      WHERE s.payer_member_id=?1
      	AND s.is_archived='false'
      	AND s.currency=?2
      GROUP BY s.payer_member_id, s.currency, m.member_name;
      """)
  Optional<MemberPaidDTO> getNetSettlementsByPayerIdAndCurrency(UUID memberId, Currency currency);

  @NativeQuery("""
      SELECT
      	s.payee_member_id AS member_id,
      	m.member_name AS member_name,
      	SUM(amount_paid) AS net_paid
      FROM settlement s
      JOIN member m
      	ON s.payee_member_id = m.member_id
      WHERE s.payee_member_id=?1
      	AND s.is_archived='false'
      	AND s.currency=?2
      GROUP BY s.payee_member_id, s.currency, m.member_name;
      """)
  Optional<MemberPaidDTO> getNetSettlementsByPayeeIdAndCurrency(UUID memberId, Currency currency);
}
