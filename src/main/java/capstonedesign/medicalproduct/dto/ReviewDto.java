package capstonedesign.medicalproduct.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
public class ReviewDto {

    private long reviewId;

    //상품상세화면에서 후기 리스트를 쓸 때 사용하는
    private String memberName;

    private long itemId;

    private String itemName;

    private String itemImageSrc;

    private LocalDateTime reviewDate;

    private String title;

    private String content;

    //상품 상세 화면으로 갈 때 해당 상품의 리뷰들 갖고오는
    @QueryProjection
    public ReviewDto(String memberName, String title, String content, LocalDateTime reviewDate) {

        this.memberName = memberName;
        this.title = title;
        this.content = content;
        this.reviewDate = reviewDate;
    }

    //로그인한 회원의 후기 리스트를 가져올때 쓰는 생성자
    @QueryProjection
    public ReviewDto(long reviewId, long itemId, String itemName, String itemImageSrc,
                     LocalDateTime reviewDate, String title, String content) {
        this.reviewId = reviewId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemImageSrc = itemImageSrc;
        this.reviewDate = reviewDate;
        this.title = title;
        this.content = content;
    }
}
