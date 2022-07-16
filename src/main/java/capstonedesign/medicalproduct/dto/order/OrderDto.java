package capstonedesign.medicalproduct.dto.order;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

//주문하는 회원 정보, 체크된 상품 데이터 담는 클래스 OrderItemDto를 리스트로 만들어 값 꺼내고 받는 클래스
@Data
public class OrderDto {

    //주문된 상품을 받는 수령인 정보
    private Long memberId;

    @NotEmpty(message = "이름은 필수입니다")
    private String name;

    @NotEmpty(message = "전화번호 필수입니다")
    @Pattern(regexp = "(01[016789])(\\d{3,4})(\\d{4})", message = "올바른 휴대폰 번호를 입력해주세요.")
    private String phoneNumber;

    @NotEmpty(message = "주소는 필수입니다")
    private String address;

    @NotEmpty(message = "상세주소는 필수입니다")
    private String addressDetail;

    private String deliveryMessage;

    @NotEmpty(message = "예금주명은 필수입니다")
    private String accountHost;

    @NotEmpty(message = "은행명은 필수입니다")
    private String bankName;

    @NotEmpty(message = "계좌번호는 필수입니다")
    private String accountNumber;

    private List<OrderItemDto> orders;
}
