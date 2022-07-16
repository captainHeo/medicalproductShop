package capstonedesign.medicalproduct.dto.ordered;

import capstonedesign.medicalproduct.domain.OrderStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderedItemDto {

    private long itemId;

    private String name;

    private String imageSrc;

    //주문 일자
    private LocalDateTime orderDate;

    private long orderId;

    private int quantity;

    private int price;

    private OrderStatus status;

    private Long reviewId;

    //complieQueryDsl해줘서 이 dto도 q파일 생성, 원하는 컬럼만 갖고올때 사용?
    //순수하지 않다, QueryDsl라이브러리에 의존하게 됨
    //주문상품리스트 가져올 때 사용
    @QueryProjection
    public OrderedItemDto(long itemId, String name, String imageSrc, LocalDateTime orderDate,
                          long orderId, int quantity, Integer price, OrderStatus status, Long reviewId) {
        this.itemId = itemId;
        this.name = name;
        this.imageSrc = imageSrc;
        this.orderDate = orderDate;
        this.orderId = orderId;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
        this.reviewId = reviewId;
    }

    //주문 상품 리스트에서 상품 하나를 주문취소하기 버튼을 누르면
    //해당 상품의 주문 아이디를 파라미터로 받아 그 상품과 같이 주문된 상품들 갖고올때 사용
    @QueryProjection
    public OrderedItemDto(long itemId, String name, String imageSrc, LocalDateTime orderDate,
                          long orderId, int quantity, Integer price, OrderStatus status) {
        this.itemId = itemId;
        this.name = name;
        this.imageSrc = imageSrc;
        this.orderDate = orderDate;
        this.orderId = orderId;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
    }
}
