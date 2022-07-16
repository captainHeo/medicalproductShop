package capstonedesign.medicalproduct.domain.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//상품 클래스
@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private long id;

    @Column(nullable = false)
    private String name;

    //이미지 경로
    @Column(nullable = false)
    private String imageSrc;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private int price;

    @Lob @Column(nullable = false)
    private String introduction;

    //상품 등급, 1등급은 의사/의료사업자만
    @Column(nullable = false)
    int rate;

    //하나의 아이템은 주문한 여러 아이템에 속함, 주인이 아닌 곳에 mappedBy
    @OneToMany(mappedBy = "item")
    private List<OrderItem> orderedItems = new ArrayList<>();

    //장바구니
    @OneToMany(mappedBy = "item")
    private List<Cart> carts = new ArrayList<>();

    //리뷰
    @OneToMany(mappedBy = "item")
    private List<Review> reviews = new ArrayList<>();
}
