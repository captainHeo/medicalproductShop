package capstonedesign.medicalproduct.Login.argumentresolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//파라미터에 사용한다는 의미
@Target(ElementType.PARAMETER)
//리플렉션 등을 활용할 수 있도록 런타임까지 애노테이션 정보가 남아있음, 실제 동작할때까지 애노테이션이 남아있어야 하므로
@Retention(RetentionPolicy.RUNTIME)
public @interface Login {
}
