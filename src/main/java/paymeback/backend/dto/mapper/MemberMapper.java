package paymeback.backend.dto.mapper;

import org.springframework.stereotype.Service;
import paymeback.backend.domain.Member;
import paymeback.backend.dto.MemberDTO;

import java.util.List;

@Service
public class MemberMapper {

  public List<Member> memberDTOsToMembers(List<MemberDTO> memberDTOS) {
    return memberDTOS.stream().map(this::memberDTOToMember).toList();
  }

  private Member memberDTOToMember(MemberDTO memberDTO) {
    Member member = new Member();
    member.setName(memberDTO.getName());

    return member;
  }
}
