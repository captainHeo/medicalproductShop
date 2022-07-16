package capstonedesign.medicalproduct;

import capstonedesign.medicalproduct.Login.argumentresolver.LoginMemberArgumentResolver;
import capstonedesign.medicalproduct.Login.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

//Interceptor를 등록하려면 WebMvcConfigurer 상속받아 오버라이딩 해야함
@Configuration
public class WebConfig implements WebMvcConfigurer {

    //@Login은 홈화면과 상품상세화면을 로그인 전과 후에 따라 다르게 보이려고 하는 목적
    //LogInterSepter는 로그인 하지 않은 사용자가 마이페이지, 장바구니 페이지로 가는걸 막는

    //LoginMemberArgumentResolver 등록, @Login을 사용하기 위한
    //이렇게 등록해야 HomeController home메서드 @login 쓸 수 있음
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver());
    }

    //LogInterceptor 등록, LoginCheckInterceptor() 등록
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)

                //컨트롤러 요청 url을 적어야함
                //회원상세, 장바구니, 회원상세에서 장바구니에 담기, 장바구니화면에서 주문하기, 회원상세에서 주문하기
                //마이페이지에서 비밀번호변경하기, 마이페이지에서 회원정보변경하기
                //주문리스트, 주문 상세, 주문 취소
                //후기리스트, 후기 수정, 후기 작성 화면
                .addPathPatterns("/members/memberDetail", "/cart/cart", "/cart/put", "/order/cartItem",
                                 "/order/itemOne", "/member/password/modify", "/member/{modifyInfo}/modify",
                                "order/orderList", "order/{id}/detail",  "order/{id}/cancel",
                                "review/reviewList", "/review/{id}/modify", "review/item/{id}");
//                .excludePathPatterns("/", "/members/add", "/login", "/logout",
//                        "/css/**", "/*.ico", "/error");
    }
}