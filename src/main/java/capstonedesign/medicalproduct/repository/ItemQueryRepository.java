package capstonedesign.medicalproduct.repository;

import capstonedesign.medicalproduct.domain.entity.Item;
import capstonedesign.medicalproduct.domain.ItemSearch;
import capstonedesign.medicalproduct.domain.entity.QItem;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

//queryDsl사용하는 클래스, 간단한 crud쿼리는 ItemRepository가 복잡한 쿼리는 여기서
@Repository
@RequiredArgsConstructor
public class ItemQueryRepository {
    
    private final JPAQueryFactory queryFactory;

    //홈화면에서 입력한 검색어가 이름에 포함되는 상품 찾기
    //findAll과 같이 반환값이 Page<item>으로 같게 해줘야해서 .fetchResults()로 데이터를 받아
    //실제값과 카운트쿼리 결과를 page구현체인 pageimple에 넣어 pageimple 반환
    public Page<Item> nameSearch(ItemSearch itemSearch, Pageable pageable) {

        //검색어 입력칸에 입력한 상품이름을 가져와 이 이름이 포함되는 상품을 찾음
        String itemName = itemSearch.getItemName();

        QItem i = QItem.item;

        //데이터를 가져오고 페이징 처리를 하기위해 QueryResults
        QueryResults<Item> findItem = queryFactory
                .select(i)
                .from(i)
                .where(i.name.contains(itemName))
                //파라미터로 들어온 pageble에서 어디서 부터 시작인지 크기는 얼마인지 뽑아오는
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                //fetch()는 반환타입이 데이터 컨텐츠를 List로 바로 가져오게됨, 내용만 가져오는
                //fetchResults()를 쓰면 컨텐츠용쿼리와 카운트용쿼리 알아서 2번 날릴 수 있는
                //조인이런게 다 붙어 최적화를 못함
                //토탈쿼리는 orderby를 지운다?
                .fetchResults();

        //getResults()로 실제 데이터
        List<Item> content = findItem.getResults();

        //getTotal()는 토탈카운트쿼리 날려 총 쿼리 2번
        long total = findItem.getTotal();

        //PageImpl이 page의 구현체
        //new PageImpl<>로 반환타입 page
        return new PageImpl<>(content, pageable, total);
    }

    //카테고리 이름과 일치하는 상품을 갖고오는
    public Page<Item> categorySearch(String category, Pageable pageable) {

        QItem i = QItem.item;

        QueryResults<Item> findItem = queryFactory
                .select(i)
                .from(i)
                .where(i.category.eq(category))
                //파라미터로 들어온 pageble에서 어디서 부터 시작인지 크기는 얼마인지 뽑아오는
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                //fetch()는 반환타입이 데이터 컨텐츠를 List로 바로 가져오게됨, 내용만 가져오는
                //fetchResults()를 쓰면 컨텐츠용쿼리와 카운트용쿼리 알아서 2번 날릴 수 있는
                //조인이런게 다 붙어 최적화를 못함
                //토탈쿼리는 orderby를 지운다?
                .fetchResults();

        //getResults()로 실제 데이터
        List<Item> content = findItem.getResults();

        //getTotal()는 토탈카운트쿼리 날려 총 쿼리 2번
        long total = findItem.getTotal();

        //PageImpl이 page의 구현체
        //new PageImpl<>로 반환타입 page
        return new PageImpl<>(content, pageable, total);
    }
}
