package com.zerobase.zerostore.controller;

import com.zerobase.zerostore.dto.CommonResponseUtil;
import com.zerobase.zerostore.dto.ReviewRequest;
import com.zerobase.zerostore.dto.ReviewResponse;
import com.zerobase.zerostore.security.UserDetailsImpl;
import com.zerobase.zerostore.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 작성
    @PostMapping
    public ResponseEntity<CommonResponseUtil<?>> createReview(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.createReview(user.getUser(), request);
        return ResponseEntity.ok(CommonResponseUtil.success("리뷰 작성 성공", response));
    }

    // 리뷰 수정
    @PatchMapping("/{reviewId}")
    public ResponseEntity<CommonResponseUtil<?>> updateReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestBody ReviewRequest request) {
        reviewService.updateReview(reviewId, user.getUser(), request.getContent(), request.getRating());
        return ResponseEntity.ok(CommonResponseUtil.success("리뷰 수정 성공"));
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<CommonResponseUtil<?>> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetailsImpl user) {
        reviewService.deleteReview(reviewId, user.getUser());
        return ResponseEntity.ok(CommonResponseUtil.success("리뷰 삭제 성공"));
    }

    // 상점 리뷰 조회
    @GetMapping("/store/{storeId}")
    public ResponseEntity<CommonResponseUtil<?>> getStoreReviews(@PathVariable Long storeId) {
        List<ReviewResponse> responses = reviewService.getReviewsByStore(storeId);
        return ResponseEntity.ok(CommonResponseUtil.success("상점 리뷰 조회 성공", responses));
    }

    // 사용자 리뷰 조회
    @GetMapping("/user")
    public ResponseEntity<CommonResponseUtil<?>> getUserReviews(
            @AuthenticationPrincipal UserDetailsImpl user) {
        List<ReviewResponse> responses = reviewService.getReviewsByUser(user.getUser());
        return ResponseEntity.ok(CommonResponseUtil.success("사용자 리뷰 조회 성공", responses));
    }
}

