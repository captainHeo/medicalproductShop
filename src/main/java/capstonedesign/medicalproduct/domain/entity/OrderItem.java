package capstonedesign.medicalproduct.domain.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

//주문된 상품 클래스
@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "ordereditem_id")
    private long id;

    //여러 주문한 상품은 하나의 주문에 속함,
    //지연로딩(부모인 order를 조회할때 orderitem 다같이 꺼내오지 않음), 항상 다 쪽이 외래키
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    Order order;

    //하나의 상품은 여러 주문한 상품에 속함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    Item item;

    //주문수량
    @Column(nullable = false)
    private int quantity;

    private int orderPrice; //주문 가격, 총 가격?

    //==생성 메서드==//
    //주문상품 객체 생성 후 값 세팅해 주문상품 반환
    //생성자를 막아놨으니 따로 생성 메서드 마련
    public static OrderItem createOrderItem(Item item, int orderPrice, int quantity) {

        OrderItem orderItem = new OrderItem();

        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setQuantity(quantity);

        return orderItem;
    }

    //==비즈니스 로직==//
    /** 주문 취소 */
    public void cancel() {
    }

    //==조회 로직==//
    /** 주문상품 전체 가격 조회 */
    public int getTotalPrice() {

        //주문가격과 수량을 곱해서 반환
        return getOrderPrice() * getQuantity();
    }
}
