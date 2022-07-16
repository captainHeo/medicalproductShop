package capstonedesign.medicalproduct.controller;

import capstonedesign.medicalproduct.Login.SessionConst;
import capstonedesign.medicalproduct.domain.entity.Member;
import capstonedesign.medicalproduct.domain.OrderSearch;
import capstonedesign.medicalproduct.domain.entity.Order;
import capstonedesign.medicalproduct.dto.ItemDetailDto;
import capstonedesign.medicalproduct.dto.RecipientInfo;
import capstonedesign.medicalproduct.dto.order.OrderDto;
import capstonedesign.medicalproduct.dto.order.OrderItemDto;
import capstonedesign.medicalproduct.dto.ordered.OrderedItemDto;
import capstonedesign.medicalproduct.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    //장바구니 화면에서 주문하기 버튼 누르면
    //장바구니에 담길 때 현재 로그인한 회원이 구매할 수 있는지 여부를 이미 체크함
    //넘어오는 input 태그의 name에 맞게 OrderDto클래스에서 OrderItemDto 타입 리스트인 orders, orders[index].CartId
    //input 태그 value값을 전달해줌.
    //form태그 안에 input태그의 이름과 메서드의 파라미터 타입의 필드 이름이 맞으면 값 대입
    @GetMapping("order/cartItem")
    public String cartItemsOrder(HttpServletRequest request, OrderDto orderDto, Model model) {

        //주문하는 회원 찾기
        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        //세션에 있는 회원 정보 값 갖고와 주문화면에 내보낼 회원정보 값 세팅
        orderDto.setMemberId(member.getId()); orderDto.setName(member.getName());
        orderDto.setPhoneNumber(member.getPhoneNumber()); orderDto.setAddress(member.getAddress());
        orderDto.setAddressDetail(member.getAddressDetail()); orderDto.setAccountHost(member.getAccountHost());
        orderDto.setBankName(member.getBankName()); orderDto.setAccountNumber(member.getAccountNumber());

        //장바구니에서 체크한 상품들 그대로 주문 리스트에 내보내기
        //OrderDto클래스가 OrderItemDto타입 리스트 가지고 있으니 getOrders로 갖고와
        List<OrderItemDto> orderItems = orderDto.getOrders();

        //주문하는 회원정보, 상품리스트정보 모델에 담기
        model.addAttribute("orderDto", orderDto);
        model.addAttribute("orderItemDto", orderItems);

        return "orders/order";
    }

    //상품상세화면에서 주문하려는 상품이 의사/ 의료사업자만이 구매할 수 있는 상품인지 판별
    //ajax로 등급을 받음, ItemDetailDto의 필드
    @ResponseBody
    @PostMapping("order/itemCheck")
    public String orderItemCheck(ItemDetailDto itemDetailDto, HttpServletRequest request) {

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

    //상품상세화면에서 주문하기 성공했을 때
    //장바구니에서 담긴 상품들을 주문할때는 장바구니에 상품이 담길때 회원과 상품 등급을 체크되서 넣어지니
    //상품상세화면에서는 바로 주문하기 버튼을 누를 때 회원과 상품 등급 체크
    @PostMapping("order/itemOne")
    public String oneItemOrder(HttpServletRequest request, OrderDto orderDto, Model model){
        //현재 로그인한 회원
        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        //세션에 있는 회원 정보 값 갖고와 주문화면에 내보낼 회원정보 값 세팅
        orderDto.setMemberId(member.getId()); orderDto.setName(member.getName());
        orderDto.setPhoneNumber(member.getPhoneNumber()); orderDto.setAddress(member.getAddress());
        orderDto.setAddressDetail(member.getAddressDetail()); orderDto.setAccountHost(member.getAccountHost());
        orderDto.setBankName(member.getBankName()); orderDto.setAccountNumber(member.getAccountNumber());

        //상품상세는 장바구니에 있다가 주문으로 가는게 아니므로 cartId없음
        //상품상세니까 하나의 상품 정보만 들어있음, 0번째 OrderItemDto객체 갖고옴
        //그래도 주문 화면에서는 상품을 반복문으로 돌리니까 리스트로?
        List<OrderItemDto> orderItemDto = orderDto.getOrders();

        //모델에 주문하는 회원정보, 하나의 상품 정보 담기
        model.addAttribute("orderDto",  orderDto);
        model.addAttribute("orderItemDto", orderItemDto);

        return "orders/order";
    }

    //주문화면에서 결제하기 버튼 누르면
    @PostMapping("order/payment")
    public String itemPayment(@Valid @ModelAttribute("orderDto") OrderDto orderDto, BindingResult bindingResult, Model model){

        //수령자 입력값에 에러 있다면
        if(bindingResult.hasErrors()) {

            //주문하는 상품들 다시 보냄, 회원 정보는 @ModelAttribute("orderDto")로 이미
            model.addAttribute("orderDto", orderDto);
            model.addAttribute("orderItemDto", orderDto.getOrders());

            //이전페이지로 redirect 하는 기능
            //return "redirect:" + request.getHeader("Referer");

            return "orders/order";
        }

        //에러 없다면 주문 메서드 파라미터에 수령자 정보와 주문 정보 담음
        orderService.order(orderDto.getMemberId(), orderDto.getName(), orderDto.getPhoneNumber(),
                orderDto.getAddress(), orderDto.getAddressDetail(),orderDto.getDeliveryMessage(),
                orderDto.getAccountHost(), orderDto.getBankName(), orderDto.getAccountNumber(), orderDto.getOrders());

        return "redirect:/";
    }

    //마이페이지에서 주문 내역 조회 버튼을 누르면, 로그인한 회원의 주문리스트를 가져오는
    //상품명과, 주문 상태를 필드로 갖고있는 OrderSearch 객체를 받아 서비스 메서드에
    @GetMapping("order/orderList")
    public String orderList(HttpServletRequest request, @ModelAttribute("orderSearch") OrderSearch orderSearch, Model model){
        //세션에 회원 객체 갖고옴
        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        //주문 상품 리스트 갖고오기, 회원의 아이디와 검색 조건을 파라미터로
        List<OrderedItemDto> orderedItems = orderService.orderItems(member.getId(), orderSearch);

        model.addAttribute("orderedItems", orderedItems);

        return "orders/orderList";
    }

    //주문 내역 리스트에서 주문 번호를 클릭하면
    @GetMapping("order/{id}/detail")
    public String orderDetail(@PathVariable("id") long orderId, Model model){

        //클릭한 주문번호에 속한 주문 상품들 가져오기
        List<OrderedItemDto> orderedItems = orderService.orderNumberOrderItems(orderId);

        model.addAttribute("orderedItems", orderedItems);

        //수령자 정보 가져오기
        Order order = orderService.recipientInfo(orderId);

        RecipientInfo recipientInfo = new RecipientInfo();

        recipientInfo.setRecipientName(order.getRecipientName());
        recipientInfo.setRecipientPhoneNumber(order.getRecipientPhoneNumber());
        recipientInfo.setRecipientAddress(order.getRecipientAddress());
        recipientInfo.setRecipientAddressDetail(order.getRecipientAddressDetail());
        recipientInfo.setDeliveryMessage(order.getDeliveryMessage());
        recipientInfo.setOrderAccountHost(order.getOrderAccountHost());
        recipientInfo.setOrderBankName(order.getOrderBankName());
        recipientInfo.setOrderAccountNumber(order.getOrderAccountNumber());

        model.addAttribute("recipientInfo", recipientInfo);

        return "orders/orderDetail";
    }

    //주문 목록 리스트 페이지에서 상품 주문 취소하기 버튼을 눌렀을 때
    @PostMapping("order/{id}/tryCancel")
    public String tryCancelOrder(@PathVariable("id") long orderId, Model model) {

        //상품의 주문 번호에 해당하는 모든 상품들 보여주기, 주문 취소 재확인
        List<OrderedItemDto> orderedItems = orderService.orderNumberOrderItems(orderId);

        model.addAttribute("orderId", orderId);
        model.addAttribute("orderedItems", orderedItems);

        return "orders/orderCancel";
    }

    //Mapping어노테이션에 있는 값은 요청 url이고 반환값 url은 뷰경로
    //만약 반환값 url 뷰경로에 컨트롤러 요청 url을 넣고 싶으면 redirect:앞에 붙여서

    //주문 취소 페이지에서 주문 취소 버튼 누르면
    @PostMapping("order/{id}/cancel")
    public String orderCancel(@PathVariable("id") long orderId) {

        //경로에 넘어온 주문 아이디에 맞는 주문 상태 취소로
        orderService.cancelOrder(orderId);

        return "redirect:/order/orderList";
    }

}
