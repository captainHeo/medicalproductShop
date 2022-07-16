package capstonedesign.medicalproduct.controller;

import capstonedesign.medicalproduct.Login.SessionConst;
import capstonedesign.medicalproduct.domain.entity.Member;
import capstonedesign.medicalproduct.dto.LoginForm;
import capstonedesign.medicalproduct.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;


@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginContorller {

    private final MemberService memberService;

    //로그인 화면으로 들어가는
    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form){
        log.info("login controller");

        return "login/loginForm";
    }

    //로그인 화면에서 로그인 버튼 눌렀을때

    //@RequestParam String loginId;
    //@RequestParam String password;

    //LoginForm form = new LoginForm();

    //form.setloginId(loginId);
    //form.setpassword(password);

    //@ModelAttribute("LoginForm") LoginForm form
    //model.addAttribute("LoginForm", form); 자동 추가
    //폼 데이터를 프로퍼티 접근법으로 받아오는 역할도 하고 뷰에 내보낼 폼 데이터를 추가해주는 역할도 동시에
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult,
                        //비회원이 페이지에 접근할때 회원인지 체크하고, 로그인 성공해 회원이면 리다이렉트해서 접근했던 페이지로 다시 넘어가게
                        //defaultValue는 다시 되돌아갈 redirectURL앞에 붙쳐줌, 기본값
                        // /뒤에 url이 없다면 /로 갈꺼고 그냥 /면 홈 화면, 있다면 /redirectURL로
                        @RequestParam(defaultValue = "/") String redirectURL, HttpServletRequest request) {

        //로그인 입력 폼에 비어있다면 다시 로그인 입력 폼으로 리다이렉트
        if (bindingResult.hasErrors()) {
            log.info("loginData Empty");

            return "login/loginForm";
        }

        //입력 폼이 비어있지 않다면 아이디 비밀번호 맞는지 확인, 틀리면 값 넘겨주지 않음
        Member loginMember = memberService.login(form);

        //아이디나 비밀번호 틀리면 다시 로그인 페이지로 리다이렉트
        if(loginMember == null) {
            log.info("loginData Wrong");

            //에러코드와 메시지, 에러파일에다가 에러코드와 메시지 정의해주지 않으면 밑에 적은 기본메시지가 출력됨
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");

            return "login/loginForm";
        }

        //로그인 성공 처리
        //세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성
        HttpSession session = request.getSession();

        //세션에 로그인한 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        //아이디 비밀번호가 맞다면 로그인 성공
        return "redirect:" + redirectURL;
    }

    //로그아웃
    //HttpSession 적용, 로그아웃해도 f12눌러보면 쿠키는 남아있음
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {

        //세션을 없애는게 목적이므로 false, false면 가져온 세션이 존재하지 않을때 새로 생성하지 않음
        HttpSession session = request.getSession(false);

        //세션이 존재한다면
        //session.invalidate()는 세션과 그 안에 있는 데이터 다 날라감
        if (session != null) {
            session.invalidate();
        }

        //홈으로 가기
        return "redirect:/";
    }
}