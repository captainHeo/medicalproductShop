package capstonedesign.medicalproduct.repository;

import capstonedesign.medicalproduct.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    //중복 아이디가 있는지 검증하기 위한
    boolean existsByLoginId(String loginId);

    //로그인시 아이디와 비번이 일치한지 확인
    Member findByLoginIdAndPassword(String loginId, String password);

    Member findByNameAndPhoneNumber(String name, String phoneNumber);
}
