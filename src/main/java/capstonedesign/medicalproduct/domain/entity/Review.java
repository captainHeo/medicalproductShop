package capstonedesign.medicalproduct.domain.entity;

import capstonedesign.medicalproduct.domain.entity.Item;
import capstonedesign.medicalproduct.domain.entity.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id @GeneratedValue
    @Column(name = "review_id")
    private long id;

    @Column(nullable = false)
    String title;

    //후기를 길게 작성할 수 있으니 @Lob로
    @Lob @Column(nullable = false)
    private String content;

    //하나의 회원은 여러 후기를 남길 수 있고
    //무조건 지연로딩, 외래키로 member_id 갖는
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    //하나의 상품에 여러 후기를 남길 수 있음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    //후기 작성한 시간
    //LocalDateTime쓰면 하이버네이트가 알아서 지원
    private LocalDateTime reviewDate;

    public static Review createReview(Member member, Item item, String title, String content) {

        Review review = new Review();

        review.setMember(member); review.setItem(item);
        review.setTitle(title); review.setContent(content);
        review.setReviewDate(LocalDateTime.now());

        return review;
    }
}
