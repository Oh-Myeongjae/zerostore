package com.zerobase.zerostore.service;

import com.zerobase.zerostore.domain.Reservation;
import com.zerobase.zerostore.domain.Review;
import com.zerobase.zerostore.domain.Store;
import com.zerobase.zerostore.domain.User;
import com.zerobase.zerostore.dto.ReviewRequest;
import com.zerobase.zerostore.dto.ReviewResponse;
import com.zerobase.zerostore.exception.CustomException;
import com.zerobase.zerostore.repository.ReservationRepository;
import com.zerobase.zerostore.repository.ReviewRepository;
import com.zerobase.zerostore.repository.StoreRepository;
import com.zerobase.zerostore.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.zerobase.zerostore.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 사용자가 리뷰를 작성하는 메서드입니다.
     * 해당 예약이 사용된 상태인지 확인하고, 사용자가 예약한 사람인지 확인한 후 리뷰를 작성합니다.
     *
     * @param user 리뷰를 작성할 사용자
     * @param request 리뷰 작성 요청 정보
     * @return 작성된 리뷰의 응답 정보
     * @throws CustomException 예약이 사용되지 않았거나, 사용자가 예약자가 아닌 경우 예외 발생
     */
    @Transactional
    public ReviewResponse createReview(User user, ReviewRequest request) {
        // 예약 존재 여부 및 사용 여부 확인
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new CustomException(RESERVATION_NOT_FOUND));

        if (!reservation.isUsed()) {
            throw new CustomException(RESERVATION_NOT_USED);
        }

        // 사용자가 예약한 사람인지 확인
        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new CustomException(ACCESS_DENIED);
        }

        // 리뷰 작성
        Review review = Review.builder()
                .user(user)
                .store(reservation.getStore())
                .reservation(reservation)
                .content(request.getContent())
                .rating(request.getRating())
                .build();

        // 리뷰 저장
        reviewRepository.save(review);

        return ReviewResponse.entityToDto(review);
    }

    /**
     * 사용자가 작성한 리뷰를 수정하는 메서드입니다.
     * 리뷰 작성자만 해당 리뷰를 수정할 수 있습니다.
     *
     * @param reviewId 수정할 리뷰의 ID
     * @param user 리뷰 작성자
     * @param content 수정할 내용
     * @param rating 수정할 평점
     * @throws CustomException 리뷰가 존재하지 않거나, 사용자가 해당 리뷰의 작성자가 아닌 경우 예외 발생
     */
    @Transactional
    public void updateReview(Long reviewId, User user, String content, Integer rating) {
        // 리뷰 존재 여부 확인
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

        // 사용자가 해당 리뷰의 작성자인지 확인
        if (!review.getUser().getId().equals(user.getId())) {
            throw new CustomException(ACCESS_DENIED);
        }

        // 리뷰 내용 및 평점 수정
        review.updateReview(content, rating);
    }

    /**
     * 사용자가 작성한 리뷰를 삭제하는 메서드입니다.
     * 리뷰 작성자 또는 상점의 소유자만 해당 리뷰를 삭제할 수 있습니다.
     *
     * @param reviewId 삭제할 리뷰의 ID
     * @param user 리뷰를 삭제하려는 사용자
     * @throws CustomException 리뷰가 존재하지 않거나, 사용자가 삭제 권한이 없는 경우 예외 발생
     */
    @Transactional
    public void deleteReview(Long reviewId, User user) {
        // 리뷰 존재 여부 확인
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

        // 삭제 권한 확인 (리뷰 작성자 또는 상점 소유자)
        boolean isOwnerOrUser = review.getUser().getId().equals(user.getId()) ||
                review.getStore().getOwner().getId().equals(user.getId());

        if (!isOwnerOrUser) {
            throw new CustomException(ACCESS_DENIED);
        }

        // 리뷰 삭제
        reviewRepository.delete(review);
    }

    /**
     * 특정 상점에 대한 모든 리뷰를 조회하는 메서드입니다.
     *
     * @param storeId 조회할 상점의 ID
     * @return 상점에 대한 리뷰 목록
     * @throws CustomException 상점이 존재하지 않으면 예외 발생
     */
    public List<ReviewResponse> getReviewsByStore(Long storeId) {
        // 상점 존재 여부 확인
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        // 해당 상점의 모든 리뷰 조회
        return reviewRepository.findByStoreId(store.getId()).stream()
                .map(ReviewResponse::entityToDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자가 작성한 모든 리뷰를 조회하는 메서드입니다.
     *
     * @param user 조회할 사용자의 정보
     * @return 사용자가 작성한 리뷰 목록
     */
    public List<ReviewResponse> getReviewsByUser(User user) {
        // 사용자가 작성한 모든 리뷰 조회
        return reviewRepository.findByUserId(user.getId()).stream()
                .map(ReviewResponse::entityToDto)
                .collect(Collectors.toList());
    }
}


