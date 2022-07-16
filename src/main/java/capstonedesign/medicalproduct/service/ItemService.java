package capstonedesign.medicalproduct.service;
import capstonedesign.medicalproduct.domain.entity.Item;
import capstonedesign.medicalproduct.domain.ItemSearch;
import capstonedesign.medicalproduct.repository.ItemRepository;
import capstonedesign.medicalproduct.repository.ItemQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    //querydsl쓰는 리포지토리
    private final ItemQueryRepository itemQueryRepository;

    //검색창이 비어있다면 모든 상품 찾아오고, 검색창에 상품이름이 적혔다면 그 값을 포함하는 상품 리스트 가져오기
    //처음 홈화면으로 가면 ItemSearch클래스 상품이름이 비어있을테니 모든 상품 출력

    //Page<T>을 타입으로 지정하면, 반드시 파라미터로 Pageable을 받아야 한다.
    //Spring Data JPA에서 페이징 처리와 정렬은 findAll() 메소드로 한다.
    //findAll() 메소드 파라미터로 pageable을 넣어주면 끝이다.
    //간단하게 Service단의 구현은 끝
    public Page<Item> findNameSearch(ItemSearch itemSearch, int page) {

        //상품 검색창에 값을 입력하면 ItemSearch name으로 값이 들어가고 검색 버튼을 누르면
        //homecontroller 다시 실행되 ItemSearch객체를 파라미터로 넣은 find메서드 실행
        //ItemSearch에서 상품이름 값을 가져와 값이 존재하면 그 값을 포함하는 이름을 갖은 상품을 찾고
        //값이 없다면 모든 상품 리스트 출력
        if(StringUtils.hasText(itemSearch.getItemName()))

            return itemQueryRepository.nameSearch(itemSearch, PageRequest.of(page,8, Sort.Direction.ASC, "id"));

        else
//        Pageable pageable = PageRequest.of(page, 1, Sort.Direction.ASC, "id");
//        findAll메서드는 파라미터로 Pageble을 받아야 하는데 PageRequest.of의 반환값 Pageable
//        파라미터를 받지 않고 그냥 모든 값들을 가져오는 findAll메서드가 있고 파라미터로 page 객체를 받는 findAll메서드이 있음
            return itemRepository.findAll(PageRequest.of(page,8, Sort.Direction.ASC, "id"));
    }

    //카테고리 이름과 일치하는 상품을 가져오는
    public Page<Item> findCategorySearch(String category, int page) {

        return itemQueryRepository.categorySearch(category, PageRequest.of(page,8, Sort.Direction.ASC, "id"));
    }

    //상품을 클릭해 상품상세화면으로 넘어갈때 사용하는, 파라미터로 들어온 id에 맞는 상품 가져오기
    public Item findOne(long id) {

        return itemRepository.findById(id).get();
    }

}
