package capstonedesign.medicalproduct.Login.interceptor;

import capstonedesign.medicalproduct.Login.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//로그인체크하는 인터셉터, 로그인 안한 사용자는 로그인 페이지로 가게하는

//인증이라는 것은 컨트롤러 호출 전에만 호출되면 된다.
//따라서 preHandle 만 구현

//인터셉터의 기능은 여기서 만들고 등록은 WebConfig에
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    //로그인체크는 이거만 있으면 된다
    //whitelist 만들어 주지 않고 인터셉터 등록할때 다 할 수 있음
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //요청에서 사용자 uri가져오고, 로그인하고 이 url로 다시 돌아가게 가져오는거임
        String requestURI = request.getRequestURI();
        log.info("인증 체크 인터셉터 실행 {}", requestURI);

        //요청에서 세션 가져오고
        HttpSession session = request.getSession();

        //세션이 null이거나 SessionConst.LOGIN_MEMBER의 이름으로 세션에서 데이터 가져온게 null이면 로그인 안된 것으로 간주,
        //로그인하면 SessionConst.LOGIN_MEMBER의 세션이름으로 그 회원 객체 넣어주니
        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
            log.info("미인증 사용자 요청");

            //로그인으로 redirect
            //whitelist가 아닌 다른url이 들어오면 requestURI는 현재 페이지 정보를 포함해서 넘김
            //미인증 사용자는 로그인 화면으로 리다이렉트 한다.
            //그런데 로그인 이후에 다시 홈으로 이동해버리면,원하는 경로를 다시 찾아가야 하는 불편함이 있음.
            //예를 들어서 주문 내역 화면을 보려고 들어갔다가 로그인 화면으로 이동하면, 로그인 이후에 다시 주문 내역 화면으로 들어가게
            //개발자 입장에서는 좀 귀찮을 수 있어도 사용자 입장에서 편리하게
            //이러한 기능을 위해 현재 요청한 경로인 requestURI 를 /login 에 쿼리 파라미터로 함께 전달한다.
            //물론 login 컨트롤러에서 로그인 성공시 해당 경로로 이동하는 기능은 추가로 개발,
            //로그인하고 다시 그 페이지로 돌아가도록
            response.sendRedirect("/login?redirectURL=" + requestURI);

            //여기서 리턴하면 다음 서블릿이나 컨트롤러 호출 안하겠다는 의미
            //미인증 사용자는 다음으로 진행하지 않고 끝!
            //앞서 redirect 를 사용했기 때문에 redirect가 응답으로 적용되고 요청이 끝난다
            //문제가 되면 false
            return false;
        }

        //정상적이면 true
        return true;
    }
}
