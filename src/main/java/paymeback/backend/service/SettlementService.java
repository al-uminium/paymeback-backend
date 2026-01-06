package paymeback.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import paymeback.backend.domain.Settlement;
import paymeback.backend.dto.request.CreateSettlementDTO;
import paymeback.backend.dto.response.MemberPaidDTO;
import paymeback.backend.exception.InvalidSettlementException;
import paymeback.backend.repository.ExpenseParticipantRepository;
import paymeback.backend.repository.SettlementRepository;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

@Service
public class SettlementService {

  private final SettlementRepository settlementRepository;

  private final ExpenseParticipantRepository participantRepository;

  private static final Logger logger = LoggerFactory.getLogger(SettlementService.class);

  public SettlementService(SettlementRepository settlementRepository, ExpenseParticipantRepository participantRepository) {
    this.settlementRepository = settlementRepository;
    this.participantRepository = participantRepository;
  }

  public Settlement createAndSaveSettlement(UUID groupId, UUID payerId, UUID payeeId, CreateSettlementDTO settlementDTO) {
    BigDecimal payerDebt = this.participantRepository.calculateMemberNetDebtByCurrency(payerId, settlementDTO.getCurrency()).getNetDebt();
    BigDecimal payeePayable = this.participantRepository.calculateMemberNetDebtByCurrency(payeeId, settlementDTO.getCurrency()).getNetDebt();

    // check if payer is in debt OR payee is payable
    if (payerDebt.compareTo(BigDecimal.ZERO) < 0 || payeePayable.compareTo(BigDecimal.ZERO) > 0) {
      throw new InvalidSettlementException("Payer must have a debt.");
    }

    // check if amount paid is bigger than amount payable e.g., A has net debt of 100. B has payable of 60. A pays 100 to B
    if (settlementDTO.getAmountPaid().compareTo(payeePayable.abs()) > 0) {
      throw new InvalidSettlementException("Amount to be paid is over payee's payable");
    }

    // check if amount paid is more than actual debt e.g., A has net debt of 60, but pays 70 to whoever.
    if (settlementDTO.getAmountPaid().compareTo(payerDebt) > 0) {
      logger.info("Payer Debt: {}", payerDebt.toString());
      logger.info("Amount paid: {}", settlementDTO.getAmountPaid());
      throw new InvalidSettlementException("Amount paid is more than net debt");
    }

    Settlement settlement = new Settlement();
    settlement.setGroupId(groupId);
    settlement.setPayerId(payerId);
    settlement.setPayeeId(payeeId);
    settlement.setAmountPaid(settlementDTO.getAmountPaid());
    settlement.setCurrency(settlementDTO.getCurrency());

    return this.settlementRepository.save(settlement);
  }

  public MemberPaidDTO getNetSettlementsByPayerIdAndCurrency(UUID id, Currency currency) {
    Optional<MemberPaidDTO> optMemberPaidDTO = this.settlementRepository.getNetSettlementsByPayerIdAndCurrency(id, currency);
    return optMemberPaidDTO.orElse(null);
  }

  public MemberPaidDTO getNetSettlementsByPayeeIdAndCurrency(UUID id, Currency currency) {
    Optional<MemberPaidDTO> optMemberPaidDTO = this.settlementRepository.getNetSettlementsByPayeeIdAndCurrency(id, currency);
    return optMemberPaidDTO.orElse(null);
  }
}
