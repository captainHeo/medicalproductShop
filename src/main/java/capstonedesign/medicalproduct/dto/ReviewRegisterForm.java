package capstonedesign.medicalproduct.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ReviewRegisterForm {

    private long reviewId;

    private long itemId;

    private String itemName;

    private String itemImageSrc;

    @NotEmpty(message = "후기 제목을 입력해주세요")
    private String title;

    @NotEmpty(message = "후기 내용을 입력해주세요")
    private String content;

    public ReviewRegisterForm() {};

    //후기 수정할때 후기 정보 받아오는 생성자
    @QueryProjection
    public ReviewRegisterForm(long reviewId, long itemId, String itemName, String itemImageSrc, String title, String content) {
        this.reviewId = reviewId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemImageSrc = itemImageSrc;
        this.title = title;
        this.content = content;
    }
}
