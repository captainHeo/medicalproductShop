package capstonedesign.medicalproduct.repository;

import capstonedesign.medicalproduct.domain.*;
import capstonedesign.medicalproduct.domain.entity.*;
import capstonedesign.medicalproduct.dto.ordered.OrderedItemDto;
import capstonedesign.medicalproduct.dto.ordered.QOrderedItemDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.springframework.util.StringUtils.*;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<OrderedItemDto> orderedItems(long memberId, OrderSearch orderSearch) {

        //쿼리와 조인문에 들어갈 객체들
        QMember m = new QMember("m");
        QItem i = new QItem("i");
        QOrder o = new QOrder("o");
        QOrderItem oi = new QOrderItem("oi");
        QReview r = new QReview("r");

        //기본 조건으로 회원의 아이디를 주고, 컬럼을 회원 테이블 기본키로 하니 조건이 안먹힘
        //그래서 주문 테이블 외래키인 회원 테이블 기본키로 해줌
        BooleanBuilder builder = new BooleanBuilder(o.member.id.eq(memberId));

        //주문 상태에 값이 있다면 동일한 주문 상태를 갖고있는 값들을 갖고옴
        if(orderSearch.getOrderStatus() != null) {
            builder.and(o.status.eq(orderSearch.getOrderStatus()));
        }

        if(hasText(orderSearch.getItemName())) {
            builder.and(i.name.contains(orderSearch.getItemName()));
        }

        List<OrderedItemDto> result = jpaQueryFactory.
                                    select( new QOrderedItemDto( i.id, i.name, i.imageSrc, o.orderDate, o.id,
                                            oi.quantity, oi.orderPrice, o.status,

                                            JPAExpressions
                                                    .select(r.id)
                                                    .from(r)
                                                    .where(r.member.id.eq(o.member.id)
                                                    .and(r.item.id.eq(i.id)) ) ) ).distinct()

                                    .from(o, oi, m)
                                    .orderBy(o.orderDate.desc())
                                    .join(o.member, m)
                                    .join(oi.order, o)
                                    .join(oi.item, i)
                                    .where(builder)
                                    //값이 여러 개면 fetch()
//                                    offset(pageable.getOffset()).
//                                    limit(pageable.getPageSize()).
                                    //fetch()는 반환타입이 데이터 컨텐츠를 List로 바로 가져오게됨, 내용만 가져오는
                                    //fetchResults()를 쓰면 컨텐츠용쿼리와 카운트용쿼리 알아서 2번 날릴 수 있는
                                    //조인이런게 다 붙어 최적화를 못함
                                    //토탈쿼리는 orderby를 지운다?
//                                    fetchResults();
                                     .fetch();

        return result;
    }

    //주문 상품 리스트에서 상품 하나를 주문취소하기 버튼을 누르면
    //해당 상품의 주문 아이디를 파라미터로 받아 그 상품과 같이 주문된 상품들 갖고옴
    public List<OrderedItemDto> orderNumberOrderItems(long orderId) {

        //쿼리와 조인문에 들어갈 객체들
        QMember member = QMember.member;
        QItem item = QItem.item;
        QOrder order = QOrder.order;
        QOrderItem orderItem = QOrderItem.orderItem;

        List<OrderedItemDto> result = jpaQueryFactory.
                select(new QOrderedItemDto(item.id, item.name, item.imageSrc, order.orderDate, order.id,
                        orderItem.quantity, orderItem.orderPrice, order.status)).distinct()

                .from(order, orderItem)
                .orderBy(order.orderDate.desc())
                .join(order.member, member)
                .join(orderItem.order, order)
                .join(orderItem.item, item)
                .where(order.id.eq(orderId))
                .fetch();

        return result;
    }
}
