package capstonedesign.medicalproduct.Login.argumentresolver;

import capstonedesign.medicalproduct.Login.SessionConst;
import capstonedesign.medicalproduct.domain.entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

//@Login과 LoginMemberArgumentResolver는 로그인한 사용자는 loginhome 안한 사용자면 home으로 가게하는, 상품 상세도

//ArgumentResolver
//등록은 WebConfig에
@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    //이것들이 파라미터를 지원하는가
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("supportsParameter 실행");

        //이 파라미터에 Login애노테이션이 있는지 물어봄
        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);

        //parameter.getParameterType()는 파라미터에서 파라미터 타입을 갖고옴, @Login 옆에 타입클래스가 Member인지 확인하는
        //Member.class.isAssignableFrom는 갖고온 타입이 Member클래스타입이냐
        boolean hasMemberType = Member.class.isAssignableFrom(parameter.getParameterType());

        //둘 다 만족하는지, 만족하면 밑에 메서드 resolveArgument 실행
        return hasLoginAnnotation && hasMemberType;
    }

    //supportsParameter() : @Login 애노테이션이 있으면서 Member 타입이면 해당 ArgumentResolver가 사용된다.
    //ArgumentResolver가 실행되었을때 어떤 값을 넣어줄것인가
    //resolveArgument() : 컨트롤러 호출 직전에 호출 되어서 필요한 파라미터 정보를 생성해준다.
    //@Login이 적용된 컨트롤러는 homecontroller이므로 이 컨트롤러 호출 전 정보를 생성해 넣어주는
    //여기서는 세션에 있는 로그인 회원 정보인 member객체를 찾아서 반환해준다.
    //이후 스프링MVC는 컨트롤러의 메서드를 호출하면서 여기에서 반환된 member객체를 파라미터에 전달해준다.
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        log.info("resolveArgument 실행");

        //HttpServletRequest가 필요하기 때문에 타입 변환, 캐스탱해줌
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        //요청에서 세션 가져옴, 로그인에 성공했으면 로그인한 회원객체를 세션과 같이 담기때문에
        //세션 없으면 로그인 안한 사용자
        //true로 하면 기존에 세션이 없으면 세션이 만들어짐, 의미 없는 세션이 만들어지지 않도록 false로
        HttpSession session = request.getSession(false);

        //세션이 null이면 회원이 아니란 의미이므로 HomeController @Login 옆 Member 객체에 null을 넣어버림
        if (session == null) {
            return null;
        }

        //세션 null이 아니면 꺼냄, 로그인할때 SessionConst.LOGIN_MEMBER이름으로 세션 등록하니
        //똑같이 이 이름인 값을 꺼냄
        //세션이 있으면 Member객체 반환,
        //반환된 멤버 HomeController @Login 옆 Member객체에 넘어감
        return session.getAttribute(SessionConst.LOGIN_MEMBER);
    }
}
