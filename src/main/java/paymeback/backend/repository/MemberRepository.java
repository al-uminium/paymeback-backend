package paymeback.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import paymeback.backend.domain.Member;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
  List<Member> findAllByGroupId(UUID groupId);
  List<Member> findAllByGroupIdAndRemovedTsIsNull(UUID groupId);
}
