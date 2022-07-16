package capstonedesign.medicalproduct.repository;

import capstonedesign.medicalproduct.domain.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

//@Repository 애노테이션 생략 가능
//컴포넌트 스캔을 스프링 데이터 JPA가 자동으로 처리
//JPA 예외를 스프링 예외로 변환하는 과정도 자동으로 처리
//엔티티 클래스, 기본키 타입을 제네릭으로 넣어주는
public interface CartRepository extends JpaRepository<Cart, Long> {

}
