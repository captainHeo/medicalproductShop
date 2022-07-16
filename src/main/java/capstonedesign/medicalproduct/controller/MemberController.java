package capstonedesign.medicalproduct.controller;

import capstonedesign.medicalproduct.Login.SessionConst;
import capstonedesign.medicalproduct.domain.entity.Member;
import capstonedesign.medicalproduct.dto.IdFindForm;
import capstonedesign.medicalproduct.dto.MemberDetailForm;
import capstonedesign.medicalproduct.dto.MemberRegisterForm;
import capstonedesign.medicalproduct.dto.PasswordUpdateDto;
import capstonedesign.medicalproduct.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@Slf4j
//final이 있는 필드만 파라미터로 넣어 생성자 만들어주는, 생성자가 하나면 @Autowired 안해줘도됨
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    //로그인화면에서 회원가입버튼 누르거나 url로 들어오면 회원가입 페이지, 버튼의 링크값이 /members/add
    @GetMapping("members/add")
    public String MemberRegisterForm(@ModelAttribute("MemberRegisterForm") MemberRegisterForm form){
        log.info("MemberRegister controller");

        return "members/addMemberForm";
    }

    //회원가입 페이지에서 회원가입 버튼 누르면
    //@ModelAttribute("MemberRegisterForm") MemberRegisterForm form로 회원가입 실패하면
    //폼에 적어둔 값 MemberRegisterForm 여기에 있으므로 다시 출력됨
    @PostMapping("members/add")
    public String MemberRegister(@Valid @ModelAttribute("MemberRegisterForm") MemberRegisterForm form,
                                 BindingResult bindingResult){

        //아이디 중복체크
        int exsist = memberService.validateDuplicateMember(form.getLoginId());

        if (exsist == 1)
            //글로벌오류
            bindingResult.reject("loginId", "입력하신 아이디는 다른 회원이 사용중입니다");

        //회원가입 입력 폼에 에러 있으면 리다이렉트
        if (bindingResult.hasErrors()) {

            log.info("MemberRegisterData Error");

            return "members/addMemberForm";
        }

        //에러 없다면
        //회원가입페이지에서 적었던 값들을 MemberRegisterForm에 담아 그 값들을 회원 객체 생성 메서드 파라미터에
        //회원가입 메서드, 회원 엔티티 저장
        memberService.join(form.getLoginId(), form.getPassword(), form.getName(), form.getPhoneNumber(),
                form.getAddress(), form.getAddressDetail(), form.getEmail(), form.getAccountHost(), form.getBankName(),
                form.getAccountNumber(), form.getHospitalName(), form.getBusinessRegisterNumber(), form.getDoctorLicenseNumber());

        //회원가입 성공하면 로그인 페이지로
        return "redirect:/login";
    }

    //아이디 찾기 화면으로
    @GetMapping("members/idFind")
    public String idFind(@ModelAttribute("idFindForm") IdFindForm idFindForm) {

        return "members/idFind";
    }

    //이름과 전화번호를 입력하고 아이디 찾기 버튼을 눌렀을때
    @PostMapping("members/tryIdFind")
    public String idFind(@Valid @ModelAttribute("idFindForm") IdFindForm idFindForm, BindingResult bindingResult,
                         Model model) {

        //이름과 전화번호중 빈 값이 있으면 다시 로그인 찾기 페이지
        if (bindingResult.hasErrors()) {

            return "members/idFind";
        }

        Member member = memberService.findId(idFindForm);

        String loginId = null;

        //해당하는 아이디가 없다면
        if(member == null) {

            model.addAttribute("result", loginId);
        //해당하는 아이디 있으면
        } else {
            loginId = member.getLoginId();
            model.addAttribute("result", loginId);
        }

        return "members/idFindResult";
    }

    //회원 상세 페이지로
    @GetMapping("members/memberDetail")
    public String memberDetail(HttpServletRequest request, Model model) {

        //요청 객체에서 세션 얻어오고
        HttpSession session = request.getSession();
        //로그인할때 세션에 넣어둔 회원 객체 가져옴
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        //회원 객체의 값들을 내보낼 회원 상세 객체에 세팅
        MemberDetailForm memberDetail = new MemberDetailForm();
        memberDetail.setLoginId(member.getLoginId());
        memberDetail.setName(member.getName()); memberDetail.setPhoneNumber(member.getPhoneNumber());
        memberDetail.setAddress(member.getAddress()); memberDetail.setAddressDetail(member.getAddressDetail());
        memberDetail.setEmail(member.getEmail());memberDetail.setAccountHost(member.getAccountHost());
        memberDetail.setBankName(member.getBankName()); memberDetail.setAccountNumber(member.getAccountNumber());

        memberDetail.setHospitalName(member.getHospitalName()); memberDetail.setBusinessRegisterNumber(member.getBusinessRegisterNumber());
        memberDetail.setDoctorLicenseNumber(member.getDoctorLicenseNumber());

        model.addAttribute("memberDetail", memberDetail);

        return "members/myPage";
    }

    //회원 상세 페이지에서 비밀번호 수정버튼 제외하고 수정버튼 아무거나 클릭했을때
    //수정버튼을 클릭한 값만 바꿔야지 해당 회원의 정보를 다 set메서드로 바꾸면 update쿼리 너무 많이 나간다?
    //그래서 어떤 수정버튼을 눌렀냐에 따라 경로에 번호를 넣어 그 번호에 따라 값을 바꾸는
    @PostMapping("member/{modifyInfo}/modify")
    public String memberInfoModify(HttpServletRequest request, @PathVariable ("modifyInfo") int modifyInfo,
                                   @Valid @ModelAttribute("memberDetail") MemberDetailForm memberDetailForm,
                                   BindingResult bindingResult, Model model) {

        //입력값에 에러가 있다면 다시 회원상세 페이지로
        if(bindingResult.hasErrors()) {

            return "members/myPage";
        }

        //입력값에 대해 에러가 없다면
        //요청 객체에서 세션 얻어오고,
        HttpSession session = request.getSession();
        //로그인할때 세션에 넣어둔 회원 객체 가져옴
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        //경로에 포함되어있는 숫자로 어떤 값이 수정되었는지 확인해 그 값 변경
        switch (modifyInfo) {
            case 1: {
                memberService.nameUpdate(member.getId(), memberDetailForm.getName());
                break;
            }
            case 2: {
                memberService.phoneNumberUpdate(member.getId(), memberDetailForm.getPhoneNumber());
                break;
            }
            case 3: {
                memberService.addressUpdate(member.getId(), memberDetailForm.getAddress());
                memberService.addressDetailUpdate(member.getId(), memberDetailForm.getAddressDetail());
                break;
            }
            case 4: {
                memberService.emailUpdate(member.getId(), memberDetailForm.getEmail());
                break;
            }
            case 5: {
                memberService.accountHostUpdate(member.getId(), memberDetailForm.getAccountHost());
                break;
            }
            case 6: {
                memberService.bankNameUpdate(member.getId(), memberDetailForm.getBankName());
                break;
            }
            case 7: {
                memberService.accountNumberUpdate(member.getId(), memberDetailForm.getAccountNumber());
                break;
            }
            case 8: {
                memberService.hospitalNameUpdate(member.getId(), memberDetailForm.getHospitalName());

                break;
            }
            case 9: {
                memberService.businessRegisterNumberUpdate(member.getId(), memberDetailForm.getBusinessRegisterNumber());
                break;
            }
            case 10: {
                memberService.doctorLicenseNumberUpdate(member.getId(), memberDetailForm.getDoctorLicenseNumber());
                break;
            }
        }

        //session.invalidate()는 세션과 그 안에 있는 데이터 다 날라감
        session.invalidate();

        //홈으로 가기
        return "redirect:/";
    }

    //비밀번호 변경 입력 폼으로
    @GetMapping("member/password/modify")
    public String passwordModifyForm(@ModelAttribute("passwordUpdateDto") PasswordUpdateDto passwordUpdateDto) {

        return "members/passwordUpdate";
    }

    //비밀번호 변경 입력 폼에서 확인버튼 누르면
    @PostMapping("member/password/modify")
    public String passwordModify(@Valid @ModelAttribute("passwordUpdateDto") PasswordUpdateDto passwordUpdateDto,
                                 BindingResult bindingResult, HttpServletRequest request) {

        //현재 로그인한 회원의 비밀번호 가져오기
        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        String memberPassword = member.getPassword();

        //비밀번호 입력 폼에 빈값이 있으면
        if(bindingResult.hasErrors()) {

            return "members/passwordUpdate";
        }
        //입력한 현재 비밀번호가 로그인한 회원의 비밀번호와 틀릴경우
        else if(!passwordUpdateDto.getPassword().equals(memberPassword)) {

            bindingResult.reject("presentPasswordWrong", "입력하신 현재 비밀번호가 틀립니다");

            return "members/passwordUpdate";
        }
        //입력한 새로운 비밀번호와 두번째로 입력한 새로운 비밀번호가 틀리면
        else if(!passwordUpdateDto.getNewPassword().equals(passwordUpdateDto.getNewPassword2())) {

            bindingResult.reject("newPasswordWrong", "신규 비밀번호와 재입력한 신규 비밀번호가 같지 않습니다");

            return "members/passwordUpdate";
        }

        //에러 없다면, 입력한 새로운 비밀번호 회원의 비밀번호에 넣기
        memberService.passwordUpdate(member.getId(), passwordUpdateDto.getNewPassword());

        return "redirect:/";
    }
}
