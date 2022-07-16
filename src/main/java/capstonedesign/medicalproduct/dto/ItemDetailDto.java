package capstonedesign.medicalproduct.dto;

import lombok.Data;

//상품상세화면에 내보낼 상품 정보
@Data
public class ItemDetailDto {

    private long id;

    private String name;

    private String imageSrc;

    private int quantity;

    private int price;

    private int totalPrice;

    private int rate;

    private String introduction;
}
