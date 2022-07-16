package capstonedesign.medicalproduct.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

//회원상세정보에 나갈 데이터
@Data
public class MemberDetailForm {

    //@NotEmpty는 String에 사용하는 어노테이션

    @NotEmpty(message = "아이디는 필수입니다")
    private String loginId;

    @NotEmpty(message = "이름은 필수입니다")
    private String name;

    //Member엔티티를 String으로 해줘도 입력값은 이 입력폼에 들어오기 때문에 int로 하면 들어올때 앞에 0지워짐
//    @NotNull(message = "전화번호 필수입니다")
    @NotEmpty(message = "전화번호 필수입니다")
    @Pattern(regexp = "(01[016789])(\\d{3,4})(\\d{4})", message = "올바른 휴대폰 번호를 입력해주세요.")
    private String phoneNumber;

    @NotEmpty(message = "주소는 필수입니다")
    private String address;

    @NotEmpty(message = "상세주소는 필수입니다")
    private String addressDetail;

    @NotEmpty(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 주소를 입력해주세요.")
//    @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotEmpty(message = "예금주명은 필수입니다")
    private String accountHost;

    @NotEmpty(message = "은행명은 필수입니다")
    private String bankName;

    @NotEmpty(message = "계좌번호는 필수입니다")
    private String accountNumber;

    //병원이름, 사업자등록번호, 의사면허번호는 필수 아님
    private String hospitalName;

    private String businessRegisterNumber;

    private Integer doctorLicenseNumber;
}
