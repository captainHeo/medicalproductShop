package capstonedesign.medicalproduct.service;

import capstonedesign.medicalproduct.domain.CartStatus;
import capstonedesign.medicalproduct.domain.entity.Cart;
import capstonedesign.medicalproduct.domain.CartSearch;
import capstonedesign.medicalproduct.domain.entity.Item;
import capstonedesign.medicalproduct.domain.entity.Member;
import capstonedesign.medicalproduct.dto.CartDto;
import capstonedesign.medicalproduct.repository.CartQueryRepository;
import capstonedesign.medicalproduct.repository.CartRepository;
import capstonedesign.medicalproduct.repository.ItemRepository;
import capstonedesign.medicalproduct.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@Service안에 @Component가 있어 @ComponentScan의 대상이 됨
@Service
//jpa의 모든 변경이나 로직들은 가급적이면 트랜잭션 안에서 실행되야함
//readOnly = true는 jpa가 조회하는데 성능을 최적화함
//db한테 읽기 전용이라 하면 리소스 너무 쓰지 마라
//public메서드에 공통적으로 적용하는
@Transactional(readOnly = true)
//@AllArgsConstructor
//final이 있는 필드만 생성자 만들어주는
@RequiredArgsConstructor
public class CartService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final CartQueryRepository cartQueryRepository;

    //홈화면에서 상품상세 가지 않고 상품카드에서 장바구니버튼 눌렀을 때

    //변경, 읽기가 아닌 쓰기에는 readOnly = true 넣으면 안됨
    //readOnly = false가 기본값이고 따로 이렇게 설정한 이게 더 우선
    @Transactional
    public long itemPut(long memberId, long itemId, int quantity) {

        Member member = memberRepository.findById(memberId).get();

        Item item = itemRepository.findById(itemId).get();

        Cart cart = Cart.createCart(member, item, quantity);

        cartRepository.save(cart);

        return cart.getId();
    }

    //로그인한 회원의 장바구니 아이템 가져오기
    public List<CartDto> cartItems (CartSearch cartSearch, Long memberId) {

        return cartQueryRepository.cartItems(cartSearch, memberId);
    }

    //장바구니 상품 리스트에서 상품 수량 플러스 버튼 눌렀을때
    @Transactional
    public void quantityPlus(long cartId){

        //url에 들어온 상품아이디로 장바구니 엔티티 조회
        Cart cart = cartRepository.findById(cartId).get();

        //조회한 엔티티 수량 갖고와 1 더해주기
        cart.setQuantity(cart.getQuantity() + 1);
    }

    //장바구니 상품 리스트에서 상품 수량 마이너스 버튼 눌렀을때
    @Transactional
    public void quantityMinus(long cartId){

        //url에 들어온 상품아이디로 장바구니 엔티티 조회
        Cart cart = cartRepository.findById(cartId).get();

        //조회한 엔티티 수량 갖고와 1 빼주기
        cart.setQuantity(cart.getQuantity() - 1);
    }

    //장바구니에서 아이템 취소 버튼 눌러 장바구니에서 삭제,
    @Transactional
    public void cartItemDelete(Long cartId) {

        cartRepository.deleteById(cartId);
    }
}
//수량 플러스 마이너스 버튼 누르면 해당 상품 아이디 url에 껴서 가져온 후
//그 아이디에 맞는 상품 디비에서 찾아오고 엔티티 하나 생성해서 세터 메서드?