package capstonedesign.medicalproduct.repository;

import capstonedesign.medicalproduct.domain.CartSearch;
import capstonedesign.medicalproduct.domain.entity.QCart;
import capstonedesign.medicalproduct.domain.entity.QItem;
import capstonedesign.medicalproduct.domain.entity.QMember;
import capstonedesign.medicalproduct.dto.CartDto;
import capstonedesign.medicalproduct.dto.QCartDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class CartQueryRepository {

    //JPAQueryFactory생성할때 엔티티매니저 객체를 생성자에 넣어줘야함
    //필드로 뺄 수 있음
    private final JPAQueryFactory queryFactory;

    //로그인한 회원의 장바구니 아이템 가져오는
    public List<CartDto> cartItems(CartSearch cartSearch, Long memberId){

        //쿼리와 조인문에 들어갈 객체들
        QMember member = QMember.member;
        QItem item = QItem.item;
        QCart cart = QCart.cart;

        //기본으로 들어가는 조건
        BooleanBuilder builder =
                new BooleanBuilder(cart.member.id.eq(memberId).and(cart.item.id.eq(item.id) ) );

        //hasText는 StringUtils.hasText
        //검색창에 검색어를 입력안하면 해당 회원이 담은 장바구니 상품 모두 보여주고
        //검색어 있다면 입력한 이름을 포함하는 상품을 검색하는걸 조건을 추가
        if (hasText(cartSearch.getItemName())) {
            builder.and(item.name.contains(cartSearch.getItemName()));
        }

        //데이터를 조회하면 tuple이란걸로 조회함, 여러 개의 타입이 있을 때 꺼내오는
        //프로젝션: select절에 가져올 대상을 정하는, 프로젝션 대상이 둘 이상이면 튜플이나 DTO로 조회
        //여기선 그냥 dto를 따로 만들어서 dto로 받고 dto에 값 세팅
        List<CartDto> result = queryFactory
                .select(new QCartDto(cart.id, cart.item.id, cart.item.name, item.imageSrc, item.price, cart.quantity, cart.status))
                .from(cart)
                //장바구니와 회원 조인
                .join(cart.member, member)
                //장바구니와 상품 조인
                .join(cart.item, item)
                //파라미터로 들어온 회원의 아이디와 장바구니 회원 아이디 동일, 장바구니 상품 아이디와 상품 아이디 동일
                .where(builder)
                //값이 여러 개면 fetch()
                .fetch();

        return result;
    }

}
