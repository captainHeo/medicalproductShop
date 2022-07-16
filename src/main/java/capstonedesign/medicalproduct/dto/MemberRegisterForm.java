package capstonedesign.medicalproduct.dto;

import lombok.Data;

import javax.validation.constraints.*;

//회원가입 페이지에서 값 입력하면 여기로 들어오고 빼내는
@Data
public class MemberRegisterForm {

    //@NotEmpty는 String에 사용하는 어노테이션

    @NotEmpty(message = "아이디는 필수입니다")
    private String loginId;

    @NotEmpty(message = "비밀번호는 필수입니다")
//    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
//    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
//    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자리여야 합니다.")
    private String password;

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
