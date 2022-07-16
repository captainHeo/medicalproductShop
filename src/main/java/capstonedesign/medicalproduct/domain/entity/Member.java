package capstonedesign.medicalproduct.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//회원 클래스
@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Member {

    @Id
    @GeneratedValue
    //member테이블의 id열과 매칭
    @Column(name = "member_id")
    private Long id;

    //로그인 ID, notnull
    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    //db에 번호 저장할때 타입 int로 지정하면 앞에 010부터 저장되므로 맨 앞에 0인 숫자는 없으므로 앞에 0삭제하고 저장됨
    //char varchar로
    //전화번호 11자리니까 앞에 0사라져도 unsigned zerofill조건 주어서 빈 자리 앞에섯부터 0으로 채울수 있게
//    @Column(nullable = false, columnDefinition = "int(11) unsigned zerofill")
    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String addressDetail;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String accountHost;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String accountNumber;

    //병원이름, 사업자등로번호, 의사면허번호는 필수 아님
    private String hospitalName;

    //사업자등록번호는 -들어감
    private String businessRegisterNumber;

    private Integer doctorLicenseNumber;

    //하나의 회원이 여러개의 주문, 일쪽 주인이 아닌 곳에 mappedBy
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Cart> carts= new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Review> reviews= new ArrayList<>();

    public static Member createMember(String loginId, String password, String name, String phoneNumber,
                  String address, String addressDetail, String email, String accountHost, String bankName, String accountNumber,
                  String hospitalName, String businessRegisterNumber, Integer doctorLicenseNumber) {

        Member member = new Member();

        member.loginId = loginId; member.password = password; member.name = name;
        member.phoneNumber = phoneNumber; member.address = address; member.addressDetail = addressDetail; member.email = email;
        member.accountHost = accountHost; member.bankName = bankName; member.accountNumber = accountNumber;
        member.hospitalName = hospitalName; member.businessRegisterNumber = businessRegisterNumber;
        member.doctorLicenseNumber = doctorLicenseNumber;

        return member;
    }
}
