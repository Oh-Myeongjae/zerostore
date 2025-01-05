package com.zerobase.zerostore.service;

import com.zerobase.zerostore.domain.Reservation;
import com.zerobase.zerostore.domain.Review;
import com.zerobase.zerostore.domain.User;
import com.zerobase.zerostore.dto.ReviewRequest;
import com.zerobase.zerostore.dto.ReviewResponse;
import com.zerobase.zerostore.exception.CustomException;
import com.zerobase.zerostore.repository.ReservationRepository;
import com.zerobase.zerostore.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.zerobase.zerostore.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;

    // 리뷰 작성
    @Transactional
    public ReviewResponse createReview(User user, ReviewRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new CustomException(RESERVATION_NOT_FOUND));

        if (!reservation.isUsed()) {
            throw new CustomException(RESERVATION_NOT_USED);
        }

        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new CustomException(ACCESS_DENIED);
        }

        Review review = Review.builder()
                .user(user)
                .store(reservation.getStore())
                .reservation(reservation)
                .content(request.getContent())
                .rating(request.getRating())
                .build();

        reviewRepository.save(review);

        return new ReviewResponse(
                review.getStore().getName(),
                review.getUser().getName(),
                review.getContent(),
                review.getRating()
        );
    }

    // 리뷰 수정
    @Transactional
    public void updateReview(Long reviewId, User user, String content, Integer rating) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new CustomException(ACCESS_DENIED);
        }

        review.updateReview(content, rating);
    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

        boolean isOwnerOrUser = review.getUser().getId().equals(user.getId()) ||
                review.getStore().getOwner().getId().equals(user.getId());

        if (!isOwnerOrUser) {
            throw new CustomException(ACCESS_DENIED);
        }

        reviewRepository.delete(review);
    }

    // 상점별 리뷰 조회
    public List<ReviewResponse> getReviewsByStore(Long storeId) {
        return reviewRepository.findByStoreId(storeId).stream()
                .map(review -> new ReviewResponse(
                        review.getStore().getName(),
                        review.getUser().getName(),
                        review.getContent(),
                        review.getRating()
                ))
                .collect(Collectors.toList());
    }

    // 사용자별 리뷰 조회
    public List<ReviewResponse> getReviewsByUser(User user) {
        return reviewRepository.findByUserId(user.getId()).stream()
                .map(review -> new ReviewResponse(
                        review.getStore().getName(),
                        review.getUser().getName(),
                        review.getContent(),
                        review.getRating()
                ))
                .collect(Collectors.toList());
    }
}

