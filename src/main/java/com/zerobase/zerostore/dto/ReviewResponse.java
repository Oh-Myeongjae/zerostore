package com.zerobase.zerostore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private String storeName;
    private String userName;
    private String content;
    private Integer rating;
}

