package com.zerobase.zerostore.dto;

import com.zerobase.zerostore.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ReviewResponse {
    private String storeName;
    private String userName;
    private String content;
    private Integer rating;

    public static ReviewResponse entityToDto(Review review){
        return ReviewResponse.builder()
                .storeName(review.getStore().getName())
                .userName(review.getUser().getName())
                .content(review.getContent())
                .rating(review.getRating())
                .build();
    }
}

