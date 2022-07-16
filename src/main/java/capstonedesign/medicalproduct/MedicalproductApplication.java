package capstonedesign.medicalproduct;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;
import java.io.*;
import java.nio.Buffer;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

@SpringBootApplication
public class MedicalproductApplication {

	public static void main(String[] args) {

		SpringApplication.run(MedicalproductApplication.class, args);

	}

	@Autowired
	EntityManager em;

	//JPAQueryFactory @Autowired로 자동 의존 주입 할때
	//EntityManager의 객체를 파라미터로 넣어준 new JPAQueryFactory(em)를 넣어줌
	@Bean
	public JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(em);
	}

}
