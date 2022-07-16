package capstonedesign.medicalproduct.controller;

import capstonedesign.medicalproduct.Login.SessionConst;
import capstonedesign.medicalproduct.domain.entity.Item;
import capstonedesign.medicalproduct.domain.entity.Member;
import capstonedesign.medicalproduct.dto.ReviewDto;
import capstonedesign.medicalproduct.dto.ReviewRegisterForm;
import capstonedesign.medicalproduct.service.ItemService;
import capstonedesign.medicalproduct.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ReviewController {

    private final ItemService itemService;
    private final ReviewService reviewService;

    //주문 상품 리스트에서 후기 작성 버튼 누르면
    @GetMapping("review/item/{id}")
    public String itemReviewWrite(@PathVariable("id") long itemId, Model model){

        //경로에 있는 상품 아이디로 상품을 찾고
        Item item = itemService.findOne(itemId);

        ReviewRegisterForm reviewRegisterForm = new ReviewRegisterForm();

        //리뷰Dto에 상품의 정보 세팅
        reviewRegisterForm.setItemId(item.getId()); reviewRegisterForm.setItemName(item.getName());
        reviewRegisterForm.setItemImageSrc(item.getImageSrc());

        model.addAttribute("reviewRegisterForm", reviewRegisterForm);

        return "reviews/reviewRegister";
    }

    //후기 작성 페이지에서 등록하기 버튼 누르면
    @PostMapping("review/register")
    public String itemReviewRegister(@Valid @ModelAttribute("reviewRegisterForm") ReviewRegisterForm reviewRegisterForm,
                                     BindingResult bindingResult, HttpServletRequest request) {

        //후기 제목과 내용에 빈 값이 있다면 다시 후기 작성 페이지로 리다이렉트
        if(bindingResult.hasErrors()) {

            return "reviews/reviewRegister";
        }

        //에러가 없다면
        //세션에 있는 회원 객체, 상품 아이디, 후기 제목, 후기 내용을 후기 등록 메서드 파라미터에 넣어줌
        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        reviewService.reviewRegister(member, reviewRegisterForm.getItemId(),
                                    reviewRegisterForm.getTitle(), reviewRegisterForm.getContent());

        return "redirect:/";
    }

    //로그인한 회원이 작성한 후기 리스트 갖고오기
    @GetMapping("review/reviewList")
    public String reviewList(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        List<ReviewDto> reviewList = reviewService.reviewList(member.getId());

        model.addAttribute("reviewList", reviewList);

        return "reviews/reviewList";
    }

    //후기 리스트에서 후기 중 수정 버튼을 눌렀을때
    @GetMapping("/review/{id}/modify")
    public String tryReviewModify(@PathVariable("id") long reviewId, Model model) {

        //경로에 있는 후기 아이디를 파라미터로
        ReviewRegisterForm reviewInfo = reviewService.tryReviewModify(reviewId);

        model.addAttribute("reviewInfo", reviewInfo);

        return "reviews/reviewModify";
    }

    //후기 수정 페이지에서 등록 버튼 누르면
    @PostMapping("/review/modify")
    public String reviewInfoModify(@Valid @ModelAttribute("reviewInfo") ReviewRegisterForm reviewModifyForm,
                                   BindingResult bindingResult) {

        //후기 수정 제목과 내용에 빈 값이 있다면 다시 수정페이지로
        if(bindingResult.hasErrors()) {

            return "reviews/reviewModify";
        }

        reviewService.reviewUpdate(reviewModifyForm.getReviewId(), reviewModifyForm.getTitle(), reviewModifyForm.getContent());

        return "redirect:/review/reviewList";
    }

    //후기 리스트에서 후기 중 삭제 버튼을 눌렀을때
    @PostMapping("/review/{id}/cancel")
    public String reviewCancel(@PathVariable("id") long reviewId) {

        reviewService.reviewCancel(reviewId);

        return "redirect:/review/reviewList";
    }

}
