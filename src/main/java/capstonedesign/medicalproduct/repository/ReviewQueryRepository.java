package capstonedesign.medicalproduct.repository;

import capstonedesign.medicalproduct.domain.entity.QItem;
import capstonedesign.medicalproduct.domain.entity.QMember;
import capstonedesign.medicalproduct.domain.entity.QReview;
import capstonedesign.medicalproduct.dto.QReviewDto;
import capstonedesign.medicalproduct.dto.QReviewRegisterForm;
import capstonedesign.medicalproduct.dto.ReviewDto;
import capstonedesign.medicalproduct.dto.ReviewRegisterForm;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    //로그인한 회원이 작성한 후기리스트를 가져오는
    //후기 아이디, 상품 아이디, 상품 이름, 상품 이미지경로, 후기 제목, 후기 내용
    public List<ReviewDto> reviewList(long memberId) {

        QMember member = QMember.member;
        QItem item = QItem.item;
        QReview review = QReview.review;

        List<ReviewDto> result = jpaQueryFactory
                                .select(new QReviewDto(review.id, item.id, item.name,
                                        item.imageSrc, review.reviewDate, review.title, review.content))
                .from(review)
                .join(review.item, item)
                .join(review.member, member)
                .where(member.id.eq(memberId))
                .fetch();

        return result;
    }

    //후기 수정하기 위해 로그인한 회원이 작성한 후기의 정보를 가져오는
    public ReviewRegisterForm tryModifyReview(long reviewId) {

        QMember member = QMember.member;
        QItem item = QItem.item;
        QReview review = QReview.review;

        ReviewRegisterForm result = jpaQueryFactory
                .select(new QReviewRegisterForm(review.id, item.id, item.name,
                        item.imageSrc, review.title, review.content))
                .from(review)
                .join(review.item, item)
                .join(review.member, member)
                .where(review.id.eq(reviewId))
                .fetchOne();

        return result;
    }

    //상품 상세 화면으로 갈 때 해당 상품의 리뷰들 갖고오는
    public List<ReviewDto> oneItemReviews(long itemId) {

        QMember member = QMember.member;
        QItem item = QItem.item;
        QReview review = QReview.review;

        List<ReviewDto> result = jpaQueryFactory
                .select(new QReviewDto(member.name, review.title, review.content, review.reviewDate))
                .from(review)
                .join(review.item, item)
                .join(review.member, member)
                .where(item.id.eq(itemId))
                .fetch();

        return result;
    }
}
