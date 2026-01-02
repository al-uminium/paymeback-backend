package paymeback.backend.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import paymeback.backend.domain.ExpenseGroup;
import paymeback.backend.domain.Member;
import paymeback.backend.dto.CreateGroupDTO;
import paymeback.backend.dto.MemberDTO;
import paymeback.backend.dto.mapper.MemberMapper;
import paymeback.backend.repository.ExpenseGroupRepository;
import paymeback.backend.repository.MemberRepository;

import java.util.Currency;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@Transactional
public class GroupManagementService {

  private final ExpenseGroupRepository expenseGroupRepository;

  private final MemberRepository memberRepository;

  private final MemberMapper memberMapper;

  private final UtilService utilService;

  private Logger logger = Logger.getAnonymousLogger();

  public GroupManagementService(ExpenseGroupRepository expenseGroupRepository, MemberRepository memberRepository, MemberMapper memberMapper, UtilService utilService) {
    this.expenseGroupRepository = expenseGroupRepository;
    this.memberRepository = memberRepository;
    this.memberMapper = memberMapper;
    this.utilService = utilService;
  }

  public String createGroup(CreateGroupDTO groupDTO){
    String response = "";
    ExpenseGroup expenseGroup = new ExpenseGroup();
    expenseGroup.setName(groupDTO.getGroupName());
    //TODO maybe add an error check to make sure currency is valid
    expenseGroup.setDefaultCurrency(Currency.getInstance(groupDTO.getDefaultCurrency()));
    expenseGroup.setLinkToken(utilService.generateLinkToken(30));
    expenseGroup.setExpiryTs(utilService.generateExpiryDate());
    try {
      expenseGroupRepository.save(expenseGroup);
    } catch (Exception ex) {
      logger.log(Level.WARNING, "Exception occurred", ex);
    }
    try {
      for (MemberDTO dto: groupDTO.getMembers()) {
        Member member = new Member();
        member.setName(dto.getName());
        member.setGroupId(expenseGroup.getId());
        memberRepository.save(member);
      }
    } catch (Exception ex) {
      logger.log(Level.WARNING, "Exception occurred", ex);
    }

    return response;
  }
}
