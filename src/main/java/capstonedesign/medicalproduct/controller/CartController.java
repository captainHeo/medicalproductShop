package capstonedesign.medicalproduct.controller;

import capstonedesign.medicalproduct.Login.SessionConst;
import capstonedesign.medicalproduct.domain.*;
import capstonedesign.medicalproduct.domain.entity.Cart;
import capstonedesign.medicalproduct.domain.entity.Item;
import capstonedesign.medicalproduct.domain.entity.Member;
import capstonedesign.medicalproduct.dto.CartDto;
import capstonedesign.medicalproduct.dto.IdAndResult;
import capstonedesign.medicalproduct.dto.ItemDetailDto;
import capstonedesign.medicalproduct.service.CartService;
import capstonedesign.medicalproduct.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CartController {

    private final ItemService itemService;
    private final CartService cartService;

    //홈 화면에서 장바구니에 담으려는 상품이 의사/ 의료사업자만이 구매할 수 있는 상품인지 판별
    @ResponseBody
    @PostMapping("home/cart/itemCheck")
    public IdAndResult homeCartItemCheck(IdAndResult idAndResult, HttpServletRequest request) {

        //현재 로그인한 회원
        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        //세션에 객체가 없으면 사용자가 로그인하지 않았으므로 로그인 화면으로 가게
        if(member == null) {
            idAndResult.setResult(0);

            return idAndResult;
        }

        //병원이름, 사업자번호, 의사면허번호중에 하나라도 비어있고 상품등급이 1이면
        if((member.getHospitalName() == null || member.getBusinessRegisterNumber() == null ||
                member.getDoctorLicenseNumber() == null) && idAndResult.getRate() == 1){

            idAndResult.setResult(1);

            return idAndResult;
        }

        idAndResult.setResult(2);

        return idAndResult;
    }

    //현재 상품을 로그인한 회원이 구매할 수 있다면
    //홈화면에서 상품상세로 가지않고 바로 장바구니버튼을 눌렀을때
    @PostMapping("cart/{itemId}/itemInstantPut")
    public String itemInstantPut(HttpServletRequest request, @PathVariable("itemId") long itemId){

        //상품을 장바구니에 넣는 회원
        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        //저장할 장바구니 객체 생성 메서드 파라미터에 현재 접속한 회원, 홈화면에서 클릭한 아이템의 아이디, 수량 1 세팅
        cartService.itemPut(member.getId(), itemId, 1);

        //홈화면으로 리다이렉트트
        return "redirect:/";
    }

    //상품 상세 화면에서 장바구니에 담으려는 상품이 의사/ 의료사업자만이 구매할 수 있는 상품인지 판별
    @ResponseBody
    @PostMapping("cart/itemCheck")
    public String detailCartItemCheck(ItemDetailDto itemDetailDto, HttpServletRequest request) {

        //현재 로그인한 회원
        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        //세션에 객체가 없으면 사용자가 로그인하지 않았으므로 로그인 화면으로 가게
        if(member == null) {

            return "0";
        }

        //병원이름, 사업자번호, 의사면허번호중에 하나라도 비어있고 상품등급이 1이면
        if((member.getHospitalName() == null || member.getBusinessRegisterNumber() == null ||
                member.getDoctorLicenseNumber() == null) && itemDetailDto.getRate() == 1){

            return "1";
        }

        return "2";
    }

    //상품 상세 화면에서 장바구니 담기 성공할때,
    //수량태그의 name을 quantity로 하여 ItemDetailDto의 quantity로 들어가게함
    @GetMapping("cart/put")
    public String itemPut(HttpServletRequest request, @ModelAttribute("itemDetailDto" )ItemDetailDto itemDetailDto,
                          Model model) {

        //장바구니 상품을 볼 회원이 누구인지
        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        //저장할 장바구니 객체 생성 메서드 파라미터에 현재 접속한 회원, 상품 상세 화면 아이템 아이디, 수량, 상태 PUT 세팅
        cartService.itemPut(member.getId(), itemDetailDto.getId(), itemDetailDto.getQuantity());

        //상품상세로 리다이렉트
        return "redirect:/item/" + itemDetailDto.getId();
    }

    //홈 화면에서 장바구니 버튼 누르면, 현재 로그인한 회원의 장바구니 리스트 가져오기
    @GetMapping("cart/cart")
    public String cartItems(HttpServletRequest request, @ModelAttribute("cartSearch") CartSearch cartSearch,
                            Model model){

        //장바구니 상품을 볼 회원이 누구인지
        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        //장바구니 검색어 필드가 있는 CartSearch 객체와 현재 로그인한 회원의 id를 파라미터로 줌
        List<CartDto> cartItems = cartService.cartItems(cartSearch, member.getId());

        model.addAttribute("cartItems", cartItems);

        return "cart/cartItems";
    }

    //장바구니 화면에서 상품 수량 증가 버튼 눌렀을때
    //상품 아이디로 조회하면 안됨, 장바구니에 같은 상품이 들어있을 수 있으니 장바구니 아이디로 조회
    @GetMapping("cart/cartItem/{id}/QuantityPlus")
    public String cartItemQuantityPlus(@PathVariable("id") Long cartId){

        //수량 수정
        cartService.quantityPlus(cartId);

        return "redirect:/cart/cart";
    }

    //장바구니 화면에서 상품 수량 감소 버튼 눌렀을때
    @GetMapping("cart/cartItem/{id}/QuantityMinus")
    public String cartItemQuantityMinus(@PathVariable("id") Long cartId){

        //수량 수정
        cartService.quantityMinus(cartId);

        return "redirect:/cart/cart";
    }

    //장바구니 리스트에서 취소 버튼 누르면
    @PostMapping("cart/{cartId}/cancel")
    public String cancelOrder(@PathVariable("cartId") Long cartId) {

        //url에 있는 장바구니id를 Long cartId에 넣고 이 상품을 장바구니에서 삭제
        cartService.cartItemDelete(cartId);

        //취소 상태를 담아 리다이렉트로 다시 주문리스트로 다시 가는
        //html url이 아닌 컨트롤러 요청 url적어주자
        return "redirect:/cart/cart";
    }
}
