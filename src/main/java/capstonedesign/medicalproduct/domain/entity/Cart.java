package capstonedesign.medicalproduct.domain.entity;

import capstonedesign.medicalproduct.domain.CartStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

//장바구니 클래스
@Entity
@Getter @Setter
//이미 엔티티를 생성하는 생성자를 따로 만들어놓음. 일반 생성자를 통해 생성할 수 있게 해놓은다면 유지보수가 여려워짐
//생성로직을 변경할때 파라미터를 추가한다거나 그럴 경우, 분산되니까
//그래서 생성자를 protected로 막는다. 생성자 만들면 컴파일오류 남
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {

    @Id @GeneratedValue
    @Column(name = "cart_id")
    private long id;

    //수량
    @Column(nullable = false)
    private int quantity;

    //장바구니 상태
    @Enumerated(EnumType.STRING)
    private CartStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public void setMember(Member member) {
        this.member = member;
        member.getCarts().add(this);
    }

    public void setItem(Item item) {
        this.item = item;
        item.getCarts().add(this);
    }

    public static Cart createCart(Member member, Item item, int quantity) {

        Cart cart = new Cart();

        cart.setMember(member); cart.setItem(item); cart.setQuantity(quantity); cart.setStatus(CartStatus.PUT);

        return cart;
    }
}
