package capstonedesign.medicalproduct.controller;

import capstonedesign.medicalproduct.Login.argumentresolver.Login;
import capstonedesign.medicalproduct.domain.entity.Item;
import capstonedesign.medicalproduct.domain.ItemSearch;
import capstonedesign.medicalproduct.domain.entity.Member;
import capstonedesign.medicalproduct.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final ItemService itemService;

    //ArgumentResolver사용
    //@Login은 직접 만든 어노테이션
    //@Login만 있으면 넌 로그인된 사용자구나 하고 세션에서 찾아서 넣어주는 과정을 이거 하나로
    //ArgumentResolver를 만들어 동작방식 바꿔줌
    //조건이 만족했을때 ModelAttribute가 아닌 우리가 만든 ArgumentResolver가 동작하게 하는

    //컨트롤러에서 공통으로 사용하는거 애노테이션 하나로 간편하게 해결
    //실행해보면, 결과는 동일하지만, 더 편리하게 로그인 회원 정보를 조회할 수 있다.

    //이렇게 ArgumentResolver 를 활용하면 공통 작업이 필요할 때 컨트롤러를 더욱 편리하게 사용할 수 있다
    //홈화면으로 갈때 로그인 했으면 로그인한 회원객체와 세션 생성해주고 LoginMemberArgumentResolver 를 통해
    //세션이 존재한다면 그 로그인한 회원 객체 loginMember객체안에 대입해주므로
    //비어있으면 일반 홈화면, 비어있지 않으면 로그인된 홈화면으로

    //상품 검색창에 값을 입력하면 ItemSearch로 값이 들어가고 검색 버튼을 누르면
    //homecontroller 다시 실행됨, th:action에 값을 안넣어 현재 그 페이지 url로 다시 요청, method방식도 생략해 get으로 함
    //ItemSearch객체를 파라미터로 넣은 find메서드 실행
    //ItemSearch에서 상품이름 값을 가져와 값이 존재하면 그 값을 포함하는 이름을 갖은 상품을 찾고
    //값이 없다면 모든 상품 리스트 출력

//    @PageableDefault을 사용해 간단하게 구현
//
//    이 어노테이션을 사용하지 않으면 Repository에 정렬 쿼리를 작성해야하고 페이징 처리, 페이지의 사이즈까지 각각 구현해야 하지만,
//    @PageableDefault 어노테이션으로 한방에 구현.
//    @PageableDefault
//    size : 한 페이지에 담을 모델의 수를 정할 수 있다. 기본 값은 10
//    sort : 정렬의 기준이 되는 속성을 정함
//    direction : 오름차순과 내림차순 중 기준을 선택할 수 있다
//    Pageable pageable : PageableDefault 값을 갖고 있는 변수를 선언

    @GetMapping("/")
    public String home(@Login Member loginMember,
                       @ModelAttribute("itemSearch") ItemSearch itemSearch, Model model,
                       @RequestParam(required = false, defaultValue = "0", value ="page") int page) {

        //검색어와 page를 파라미터로 넣어 결과 가져옴
        Page<Item> listPage = itemService.findNameSearch(itemSearch, page);

        int totalPage = listPage.getTotalPages(); // 총 페이지 수

        log.info("home controller");

        //세션에 회원 데이터가 없으면 home
        if (loginMember == null) {

            //검색 조건을 넣어, 검색 조건 값이 비어있다면 모든 상품 갖고오는
            //getContent() 실제 데이터 갖고오는
            model.addAttribute("items",  listPage.getContent());

            //totalPage는 총 페이지 수 반환, 100개 데이터 10개씩 쪼개지면 10페이지 반환되는
            model.addAttribute("totalPage", totalPage);
            return "home";
        }
        //세션에 회원 데이터가 있으면 loginhome

        model.addAttribute("items",  listPage.getContent());
        model.addAttribute("totalPage", totalPage);

        return "loginhome";
    }

}
