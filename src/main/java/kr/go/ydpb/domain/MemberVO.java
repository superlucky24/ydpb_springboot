package kr.go.ydpb.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.time.LocalDate;

@Data
public class MemberVO {


    @NotBlank(message = "아이디는 필수입니다.")
    @Pattern(regexp = "^[a-z][a-z0-9]{5,19}$",
            message = "아이디는 영문 소문자와 숫자 조합 6~20자여야 합니다.")
    private String memId;

    @NotBlank(message = "ID는 필수 사항입니다.")
    @Pattern(regexp = "^[가-힣]{1,16}$|^[A-Za-z]{1,50}$",
            message = "이름은 한글 16자 또는 영문 50자까지 가능합니다.")
    private String memName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate memBirth;
    private String memGender;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~15자입니다.")
    private String memPassword;

    @NotBlank(message = "주소는 필수입니다.")
    private String memAddress;

    @NotBlank(message = "주소는 필수입니다.")
    private String memAddressDetail;

    @Pattern(
            regexp = "^$|^(02|031|032|033|041|042|043|044|051|052|053|054|055|061|062|063|064)\\d{7,8}$",
            message = "유효한 유선전화 번호를 입력해주세요."
    )
    private String memTel;

    @NotBlank(message = "휴대폰 번호는 필수입니다.")
    @Pattern(
            regexp = "^(010)\\d{8}$",
            message = "유효한 휴대폰 번호를 입력해주세요. (010XXXXXXXX)"
    )
    private String memPhone;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String memEmail;

    private String memNews;
    private int memRole;
    // 가입일 추가
    private java.util.Date memRegDate;

    private String loginType;
}
