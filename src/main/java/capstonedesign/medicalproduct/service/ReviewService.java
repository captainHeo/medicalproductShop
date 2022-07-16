package capstonedesign.medicalproduct.service;

import capstonedesign.medicalproduct.domain.entity.Item;
import capstonedesign.medicalproduct.domain.entity.Member;
import capstonedesign.medicalproduct.domain.entity.Review;
import capstonedesign.medicalproduct.dto.ReviewDto;
import capstonedesign.medicalproduct.dto.ReviewRegisterForm;
import capstonedesign.medicalproduct.repository.ItemRepository;
import capstonedesign.medicalproduct.repository.ReviewQueryRepository;
import capstonedesign.medicalproduct.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final ItemRepository itemRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewQueryRepository reviewQueryRepository;

    //후기 등록
    //삽입은 readOnly아님
    @Transactional
    public void reviewRegister(Member member, long itemId, String title, String content) {

        Item item = itemRepository.findById(itemId).get();

        Review review = Review.createReview(member, item, title, content);

        reviewRepository.save(review);
    }

    //로그인한 회원이 작성한 후기 리스트들 갖고오기
    public List<ReviewDto> reviewList(long memberId) {

        return reviewQueryRepository.reviewList(memberId);
    }

    //회원 아이디와 후기 아이디를 파라미터로 주어 수정할 후기 정보를 가져오는
    public ReviewRegisterForm tryReviewModify(long reviewId) {

        return reviewQueryRepository.tryModifyReview(reviewId);
    }

    //후기 정보 수정, 파라미터로 들어온 후기 아이디로 후기 엔티티 가져오고,
    //파라미터로 들어온 제목과 내용으로 원래 있던 정보 변경
    @Transactional
    public void reviewUpdate(long reviewId, String title, String content){

        Review review = reviewRepository.findById(reviewId).get();
        review.setTitle(title); review.setContent(content); review.setReviewDate(LocalDateTime.now());
    }

    //후기 삭제하기
    @Transactional
    public void reviewCancel(long reviewId) {

        reviewRepository.deleteById(reviewId);
    }

    //상품 상세 화면으로 갈 때 해당 상품의 리뷰들 갖고오는
    public List<ReviewDto> oneItemReviews(long itemId) {

        return reviewQueryRepository.oneItemReviews(itemId);
    }
}
