package capstonedesign.medicalproduct.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

//비밀번호 변경할때 사용하는
@Data
public class PasswordUpdateDto {

    @NotEmpty(message = "현재 비밀번호를 입력해주세요")
    private String password;

    @NotEmpty(message = "새로운 비밀번호를 입력해주세요")
    private String newPassword;

    @NotEmpty(message = "새로운 비밀번호를 다시 입력해주세요")
    private String newPassword2;
}
