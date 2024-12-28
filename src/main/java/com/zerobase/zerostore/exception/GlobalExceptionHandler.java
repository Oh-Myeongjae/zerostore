package com.zerobase.zerostore.exception;

import com.zerobase.zerostore.dto.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.zerobase.zerostore.type.ErrorCode.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public CommonResponse<Object> handleException(CustomException e) {
        return CommonResponse.builder()
                .status(e.getStatus())
                .message(e.getErrorMessage())
                .build();
    }

    @ExceptionHandler(Exception.class)
    public CommonResponse<Object> handleException(Exception e) {
        return CommonResponse.builder()
                .status(INTERNAL_SERVER_ERROR.getStatus())
                .message(INTERNAL_SERVER_ERROR.getDescription())
                .build();
    }
}
