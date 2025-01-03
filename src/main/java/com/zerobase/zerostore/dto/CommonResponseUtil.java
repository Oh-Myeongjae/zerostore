package com.zerobase.zerostore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL) // Null 값은 응답에서 제외
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CommonResponseUtil<T> {
    private int status;
    private String message;
    private T data;

    public static <T> CommonResponseUtil<T> success(String message, T data) {
        return CommonResponseUtil.<T>builder()
                .message(message)
                .status(200)
                .data(data)
                .build();
    }
    public static <T> CommonResponseUtil<T> success(String message) {
        return CommonResponseUtil.<T>builder()
                .message(message)
                .status(200)
                .data(null)
                .build();
    }

    public static <T> CommonResponseUtil<T> error(int status, String message) {
        return CommonResponseUtil.<T>builder()
                .message(message)
                .status(status)
                .data(null)
                .build();
    }
}

