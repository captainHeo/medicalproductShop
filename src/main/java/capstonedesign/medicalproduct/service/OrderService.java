package capstonedesign.medicalproduct.service;

import capstonedesign.medicalproduct.domain.*;
import capstonedesign.medicalproduct.domain.entity.Item;
import capstonedesign.medicalproduct.domain.entity.Member;
import capstonedesign.medicalproduct.domain.entity.Order;
import capstonedesign.medicalproduct.domain.entity.OrderItem;
import capstonedesign.medicalproduct.dto.order.OrderItemDto;
import capstonedesign.medicalproduct.dto.ordered.OrderedItemDto;
import capstonedesign.medicalproduct.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    //서비스 게층은 단순히 엔티티 조회하고 연결하고 호출해주는 정도만

    /** 주문 */
    //저장할 수 있게 readOnly = true 적용 안함
    //컨트롤러에서 이 메서드 호출, 이 메서드는 Order클래스의 생성자 호출
    @Transactional
    public Long order(Long memberId, String recipientName, String recipientPhoneNumber,
                      String recipientAddress, String recipientAddressDetail, String deliveryMessage, String accountHost,
                      String bankName, String accountNumber, List<OrderItemDto> orders) {

        //엔티티 조회
        //id에 맞는 Member객체 Item객체 찾기, 주문하는 회원과 주문되는 상품이 뭔지
        //스프링 데이터 JPA는 그냥 값 반환하면 Optional로 받아야해서 .get()으로, 제공해주는 메서드에 한해서만
        Member member = memberRepository.findById(memberId).get();

        //하나의 주문에 속해있는 주문 상품들 리스트 생성
        List<OrderItem> orderItems = new ArrayList<>();

        //파라미터로 들어온 주문 화면의 주문 상품 리스트를 갖고와
        for (OrderItemDto orderInfo : orders) {

            //장바구니에서 체크한 상품 주문했을때 체크한 상품 삭제
            //상품 상세 화면에서 주문하기 버튼을 누르면 장바구니 아이디 없을 수도 있으니 비어있는지 판별
            if(orderInfo.getCartId() != null)
                cartRepository.deleteById(Long.valueOf(orderInfo.getCartId()));

            //주문하는 상품의 아이디로 상품 조회하여 상품 객체 생성
            Item item = itemRepository.findById(orderInfo.getItemId()).get();

            //주문하는 상품 객체과 가격 수량 세팅해 주문에 넣을 주문상품 객체 생성, 이외에 다른 생성방법은 막아야함
            OrderItem orderItem = OrderItem.createOrderItem(item, orderInfo.getTotalPrice(), orderInfo.getQuantity());

            //생성한 주문 상품을 주문 상품 리스트에 넣음
            orderItems.add(orderItem);
        }

        //주문 상품 리스트에 주문 상품을 다 넣은 후 주문하는 회원 엔티티, 수령자 정보, 주문 상품 리스트를 주문 생성 메서드 파라미터로 넣어
        //주문 엔티티 생성
        Order order = Order.createOrder(member, recipientName, recipientPhoneNumber, recipientAddress, recipientAddressDetail,
                                        deliveryMessage, accountHost, bankName, accountNumber, orderItems);

        //주문 저장, orderitem 자동으로 퍼시스트
        //다른 것이 참조할 수 없는 프라이빗인 경우에 쓰면 좋다
        //다른데서도 참조하고 갖다 쓰면 cascade 막 쓰면 안됨
        //별도의 repository를 생성해서 퍼시스트를 개별적으로 하는게 낫다
        //다른데는 참조하는게 없고 라이프사이클 자체를 퍼시스트 할때 같이 해야하는
        orderRepository.save(order);

        return order.getId();
    }

    //로그인한 회원의 주문 상품 리스트를 갖고오는
    public List<OrderedItemDto> orderItems(long memberId, OrderSearch orderSearch) {
        return orderQueryRepository.orderedItems(memberId, orderSearch);
    }

    //주문 상품 리스트에서 상품 하나를 주문 취소하기 버튼 눌러 해당 상품과 같이 주문된 상품들 갖고오는
    public List<OrderedItemDto> orderNumberOrderItems(long orderId) {
        return orderQueryRepository.orderNumberOrderItems(orderId);
    }

    /** 주문 취소 */
    @Transactional
    public void cancelOrder(Long orderId) {

        //주문 엔티티 조회
        Order order = orderRepository.findById(orderId).get();

        //주문취소
        order.setStatus(OrderStatus.CANCEL);
    }

    //주문 아이디에 맞는 주문 상품 수령자 정보 가져오기
    public Order recipientInfo(long orderId) {

        return orderRepository.findById(orderId).get();
    }
}
//주문 서비스의 주문과 주문 취소 메서드를 보면 비즈니스 로직 대부분이 엔티티에 있다.
//서비스 계층 은 단순히 엔티티에 필요한 요청을 위임하는 역할을 한다.
//이처럼 엔티티가 비즈니스 로직을 가지고 객체 지향의 특성을 적극 활용하는 것을 도메인 모델 패턴
//jpa나 orm들을 쓰때 많이 사용
//반대로 엔티티에는 비즈니스 로직이 거의 없고
//서비스 계층에서 대부분의 비즈니스 로직을 처리하는 것을 트랜잭션 스크립트 패턴, sql을 다룰때 많이 사용
