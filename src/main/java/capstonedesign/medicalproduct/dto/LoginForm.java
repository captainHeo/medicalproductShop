package capstonedesign.medicalproduct.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

//로그인 페이지에서 값을 입력하면 여기로 들어감
@Data
public class LoginForm {

    //입력칸에서 입력한 값이 들어와 비어있다면 경고문구
    @NotEmpty(message = "ID는 필수 입니다")
    private String loginId;

    @NotEmpty(message = "비밀번호는 필수 입니다")
    private String password;
}
