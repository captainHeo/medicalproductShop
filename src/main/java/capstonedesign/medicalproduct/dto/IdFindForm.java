package capstonedesign.medicalproduct.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class IdFindForm {

    @NotEmpty(message = "이름은 필수입니다")
    private String name;

    @NotEmpty(message = "전화번호 필수입니다")
    @Pattern(regexp = "(01[016789])(\\d{3,4})(\\d{4})", message = "올바른 휴대폰 번호를 입력해주세요.")
    private String phoneNumber;
}
