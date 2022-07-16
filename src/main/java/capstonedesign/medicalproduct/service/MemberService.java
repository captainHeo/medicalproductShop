package capstonedesign.medicalproduct.service;

import capstonedesign.medicalproduct.dto.IdFindForm;
import capstonedesign.medicalproduct.dto.LoginForm;
import capstonedesign.medicalproduct.domain.entity.Member;
import capstonedesign.medicalproduct.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    //로그인
    public Member login(LoginForm loginForm) {

        //로그인할때 입력한 아이디, 비번 가져와 sql문 파라미터에 넣어줌
        String loginAttemptId = loginForm.getLoginId();
        String loginAttemptPassword = loginForm.getPassword();

        Member loginMember = memberRepository.findByLoginIdAndPassword(loginAttemptId, loginAttemptPassword);

        return loginMember;
    }

    //아이디 찾기
    public Member findId(IdFindForm idFindForm) {

        //파라미터로 넘어온 이름과 전화번호에 해당하는 아이디가 있는지 확인
        String name = idFindForm.getName();
        String phoneNumber = idFindForm.getPhoneNumber();

        Member findMember = memberRepository.findByNameAndPhoneNumber(name, phoneNumber);

        return findMember;
    }

    //회원가입
    //변경, 읽기가 아닌 쓰기에는 readOnly = true 넣으면 안됨
    //readOnly = false가 기본값이고 따로 이렇게 설정한 이게 더 우선
    @Transactional
    public void join(String loginId, String password, String name, String phoneNumber,
                     String address, String addressDetail, String email, String accountHost, String bankName, String accountNumber,
                     String hospitalName, String businessRegisterNumber, Integer doctorLicenseNumber) {

        Member member = Member.createMember(loginId, password, name, phoneNumber, address, addressDetail, email, accountHost,
                                            bankName, accountNumber, hospitalName, businessRegisterNumber, doctorLicenseNumber);
        //중복되지 않으면 저장소에 저장
        memberRepository.save(member);
    }

    //중복 회원 검증 메서드
    public int validateDuplicateMember(String joinId) {

        //파라미터로 들어온 Member객체의 아이디가 db에도 존재하는지 비교
        boolean exist = memberRepository.existsByLoginId(joinId);

        //존재한다면
        if (exist) {
            return 1;
        }
        else
            return 0;
    }

    //조회만 readOnly, 변경은 아님
    //비밀번호 변경
    @Transactional
    public void passwordUpdate(long id, String password) {
        Member member = memberRepository.findById(id).get();
        member.setPassword(password);
    }

    //이름 변경
    @Transactional
    public void nameUpdate(long id, String name) {
        Member member = memberRepository.findById(id).get();
        member.setName(name);
    }

    //번호 변경
    @Transactional
    public void phoneNumberUpdate(long id, String phoneNumber) {
        Member member = memberRepository.findById(id).get();
        member.setPhoneNumber(phoneNumber);
    }

    //주소 변경
    @Transactional
    public void addressUpdate(long id, String address) {
        Member member = memberRepository.findById(id).get();
        member.setAddress(address);
    }

    //상세주소 변경
    @Transactional
    public void addressDetailUpdate(long id, String addressDetail) {
        Member member = memberRepository.findById(id).get();
        member.setAddressDetail(addressDetail);
    }

    //이메일 변경
    @Transactional
    public void emailUpdate(long id, String email) {
        Member member = memberRepository.findById(id).get();
        member.setEmail(email);
    }

    //예금주 변경
    @Transactional
    public void accountHostUpdate(long id, String accountHost) {
        Member member = memberRepository.findById(id).get();
        member.setAccountHost(accountHost);
    }

    //은행명 변경
    @Transactional
    public void bankNameUpdate(long id, String bankName) {
        Member member = memberRepository.findById(id).get();
        member.setBankName(bankName);
    }

    //계좌번호 변경
    @Transactional
    public void accountNumberUpdate(long id, String accountNumber) {
        Member member = memberRepository.findById(id).get();
        member.setAccountNumber(accountNumber);
    }

    //병원/ 사업체명 변경
    @Transactional
    public void hospitalNameUpdate(long id, String hospitalName) {
        Member member = memberRepository.findById(id).get();
        member.setHospitalName(hospitalName);
    }

    //사업자등록번호 변경
    @Transactional
    public void businessRegisterNumberUpdate(long id, String businessRegisterNumber) {
        Member member = memberRepository.findById(id).get();
        member.setBusinessRegisterNumber(businessRegisterNumber);
    }

    //의사면허번호 변경
    @Transactional
    public void doctorLicenseNumberUpdate(long id, int doctorLicenseNumber) {
        Member member = memberRepository.findById(id).get();
        member.setDoctorLicenseNumber(doctorLicenseNumber);
    }
}
