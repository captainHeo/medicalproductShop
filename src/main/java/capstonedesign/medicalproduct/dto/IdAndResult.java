package capstonedesign.medicalproduct.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//홈화면에서 상품 장바구니에 담으려할때 사용하는
@Data
public class IdAndResult {

    long itemId;

    int rate;

    int result;
}
