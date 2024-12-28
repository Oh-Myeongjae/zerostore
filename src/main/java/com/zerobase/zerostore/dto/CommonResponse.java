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
public class CommonResponse<T> {
    private int status;
    private String message;
    private T data;
}

