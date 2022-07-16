package capstonedesign.medicalproduct.domain.entity;

import capstonedesign.medicalproduct.domain.OrderStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//실무에서는 가급적 Getter는 열어두고, Setter는 꼭 필요한 경우에만 사용하는 것을 추천
//이론적으로 Getter, Setter 모두 제공하지 않고, 꼭 필요한 별도의 메서드를 제공하는게 가장 이상적
//하지만 실무에서 엔티티의 데이터는 조회할 일이 너무 많으므로, Getter의 경우 모두 열어두는 것이 편리
//Getter는 아무리 호출해도 호출 하는 것 만으로 어떤 일이 발생하지는 않는다. 하지만 Setter는 문제가 다르다.
//Setter를 호출하면 데이터가 변한다. Setter를 막 열어두면 가까운 미래에 엔티티에가 도대체 왜 변경되는지 추적하기 점점 힘들어진다.
//그래서 엔티티를 변경할 때는 Setter 대신에 변경 지점이 명확하도록 변경을 위한 비즈니스 메서드를 별도로 제공

//주문 클래스
@Entity
@Getter @Setter
//Order 라는 단어는 "ORDER BY" 의 예약어
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private long id;

    //여러개의 주문 하나의 회원, 항상 다 쪽이 외래키를 가짐
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String recipientName;

    //수령자 정보
    //db에 번호 저장할때 타입 int로 지정하면 앞에 010부터 저장되므로 맨 앞에 0인 숫자는 없으므로 앞에 0삭제하고 저장됨
    //char varchar로
    @Column(nullable = false)
    private String recipientPhoneNumber;

    @Column(nullable = false)
    private String recipientAddress;

    @Column(nullable = false)
    private String recipientAddressDetail;

    //배송메시지 필수아님
    private String deliveryMessage;

    @Column(nullable = false)
    private String orderAccountHost;

    @Column(nullable = false)
    private String orderBankName;

    @Column(nullable = false)
    private String orderAccountNumber;

    //하나의 주문이 여러 개의 주문상품들, 주인 아닌 쪽이 mappedby
    //cascade = CascadeType.ALL는 orderItems에 값을 넣어두고 order저장하면 같이 저장됨
    //OrderItem들이 담겨있는 리스트에 하나하나가 다 저장되는
    //order를 persist하면 OrderItem도 같이
    //참조하는게 딱 주인이 프라이빗 오너인 경우에만, 참조해서 쓰는곳이 하나일떄만
    //중요해서 다른 곳에서도 참조하고 갖다쓰면 cascade 쓰면 안됨
    //복잡하게 얽혀돌아가므로
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    //주문한 시간
    //LocalDateTime쓰면 하이버네이트가 알아서 지원
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문상태 [ORDER, CANCEL]

    //Member member1 = new Member();
    //Order order = new Order();
    //member1.getOrders().add(order);
    //order.setMember(member);

    //주문도메인이 가장 중요, 비즈니스 로직들이 얽혀 돌아감
    //==연관관계 메서드==//
    //회원이 파라미터로 들어오면 외래키값 member에 넣어주고, 그 회원의 주문에 현재 주문 추가
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    //파라미터로 들어온 주문된 상품을 주문한 상품 리스트에 추가하고 어떤 주문에 속해있는지 세팅
    //주문한 상품 리스트는 Order객체를 저장할때 같이 저장되므로 파라미터로 들어오는 주문된 상품 객체는 필드들이 이미 세팅되어있음
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    //==주문 생성 메서드==//
    //주문은 회원, 수령자 정보를 파라미터로
    //뭔가 생성하는 지점 바꾸려면 이 메서드만 바꾸면 됨
    public static Order createOrder(Member member, String recipientName, String recipientPhoneNumber,
                                    String recipientAddress, String recipientAddressDetail, String deliveryMessage,
                                    String orderAccountHost, String orderBankName, String orderAccountNumber,
                                    List<OrderItem> orderItems) {

        //주문 객체 생성해 파라미터로 들어온 값들 세팅
        Order order = new Order();

        order.setMember(member); order.setRecipientName(recipientName); order.setRecipientPhoneNumber(recipientPhoneNumber);
        order.setRecipientAddress(recipientAddress); order.setRecipientAddressDetail(recipientAddressDetail);
        order.setDeliveryMessage(deliveryMessage); order.setOrderAccountHost(orderAccountHost);
        order.setOrderBankName(orderBankName); order.setOrderAccountNumber(orderAccountNumber);

        //여러 개 받은 주문물품들 for each문으로 하나씩 Order객체에 추가
        //현재 주문에 해당하는 주문 상품 리스트에 추가
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        //이 주문의 상태 order로 세팅하고 날짜도 현재 시간으로 세팅
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    //==비즈니스 로직==//
    /** 주문 취소 */
    public void cancel() {

        //취소가 가능하면 상태를 취소로 바꾸기
        this.setStatus(OrderStatus.CANCEL);

    }

    /** 전체 주문 가격 조회 */
    public int getTotalPrice() {
        int totalPrice = 0;

        for (OrderItem orderItem : orderItems) {
            //각 물품의 가격을 더해
            totalPrice += orderItem.getTotalPrice();
        }

        return totalPrice;

    }
}
