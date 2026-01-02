package paymeback.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import paymeback.backend.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
}
