package com.zerobase.zerostore.controller;

import com.zerobase.zerostore.dto.CommonResponseUtil;
import com.zerobase.zerostore.dto.ReviewRequest;
import com.zerobase.zerostore.dto.ReviewResponse;
import com.zerobase.zerostore.security.UserDetailsImpl;
import com.zerobase.zerostore.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Review", description = "리뷰 관리 API")
@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 사용자가 새로운 리뷰를 작성하는 메서드입니다.
     * 사용자는 로그인 후, 상점에 대한 리뷰를 작성할 수 있습니다.
     *
     * @param user 인증된 사용자 정보
     * @param request 예약 번호와 작성할 리뷰 내용 및 평점
     * @return 리뷰 작성 성공 메시지와 작성된 리뷰 정보
     */
    @Operation(summary = "리뷰 작성", description = "리뷰를 작성합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<CommonResponseUtil<?>> createReview(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.createReview(user.getUser(), request);
        return ResponseEntity.ok(CommonResponseUtil.success("리뷰 작성 성공", response));
    }

    /**
     * 사용자가 기존의 리뷰 내용을 수정하는 메서드입니다.
     * 사용자는 로그인 후, 본인이 작성한 리뷰를 수정할 수 있습니다.
     *
     * @param reviewId 수정할 리뷰의 ID
     * @param user 인증된 사용자 정보
     * @param request 수정할 리뷰의 새로운 내용 및 평점
     * @return 리뷰 수정 성공 메시지
     */
    @Operation(summary = "리뷰 수정", description = "리뷰의 내용을 수정합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/{reviewId}")
    public ResponseEntity<CommonResponseUtil<?>> updateReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestBody ReviewRequest request) {
        reviewService.updateReview(reviewId, user.getUser(), request.getContent(), request.getRating());
        return ResponseEntity.ok(CommonResponseUtil.success("리뷰 수정 성공"));
    }

    /**
     * 사용자가 본인이 작성한 리뷰를 삭제하는 메서드입니다.
     * 사용자는 로그인 후, 본인이 작성한 리뷰를 삭제할 수 있습니다.
     *
     * @param reviewId 삭제할 리뷰의 ID
     * @param user 인증된 사용자 정보
     * @return 리뷰 삭제 성공 메시지
     */
    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<CommonResponseUtil<?>> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetailsImpl user) {
        reviewService.deleteReview(reviewId, user.getUser());
        return ResponseEntity.ok(CommonResponseUtil.success("리뷰 삭제 성공"));
    }

    /**
     * 특정 상점에 대한 리뷰를 조회하는 메서드입니다.
     * 사용자는 상점 ID를 제공하여 해당 상점의 모든 리뷰를 조회할 수 있습니다.
     *
     * @param storeId 조회할 상점의 ID
     * @return 특정 상점의 모든 리뷰 목록
     */
    @Operation(summary = "상점 리뷰 조회", description = "특정 상점의 리뷰를 조회합니다.")
    @GetMapping("/store/{storeId}")
    public ResponseEntity<CommonResponseUtil<?>> getStoreReviews(@PathVariable Long storeId) {
        List<ReviewResponse> responses = reviewService.getReviewsByStore(storeId);
        return ResponseEntity.ok(CommonResponseUtil.success("상점 리뷰 조회 성공", responses));
    }

    /**
     * 로그인된 사용자가 본인이 작성한 리뷰 목록을 조회하는 메서드입니다.
     * 사용자는 본인이 작성한 모든 리뷰를 조회할 수 있습니다.
     *
     * @param user 인증된 사용자 정보
     * @return 사용자가 작성한 리뷰 목록
     */
    @Operation(summary = "사용자 리뷰 조회", description = "사용자의 리뷰를 조회합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/user")
    public ResponseEntity<CommonResponseUtil<?>> getUserReviews(
            @AuthenticationPrincipal UserDetailsImpl user) {
        List<ReviewResponse> responses = reviewService.getReviewsByUser(user.getUser());
        return ResponseEntity.ok(CommonResponseUtil.success("사용자 리뷰 조회 성공", responses));
    }
}

