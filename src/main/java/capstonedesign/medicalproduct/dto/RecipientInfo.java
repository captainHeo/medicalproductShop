package capstonedesign.medicalproduct.dto;

import lombok.Data;

import javax.persistence.Column;

//주문 상세 정보에 내보낼 주문 상품 수령자 정보
@Data
public class RecipientInfo {

    private String recipientName;

    private String recipientPhoneNumber;

    private String recipientAddress;

    private String recipientAddressDetail;

    private String deliveryMessage;

    private String orderAccountHost;

    private String orderBankName;

    private String orderAccountNumber;
}
