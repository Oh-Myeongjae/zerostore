package com.zerobase.zerostore.exception;

import com.zerobase.zerostore.dto.CommonResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.zerobase.zerostore.type.ErrorCode.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public CommonResponseUtil<Object> handleException(CustomException e) {
        return CommonResponseUtil.builder()
                .status(e.getStatus())
                .message(e.getErrorMessage())
                .build();
    }

    @ExceptionHandler(Exception.class)
    public CommonResponseUtil<Object> handleException(Exception e) {
        log.error("서버오류 발생",e);
        return CommonResponseUtil.builder()
                .status(INTERNAL_SERVER_ERROR.getStatus())
                .message(INTERNAL_SERVER_ERROR.getDescription())
                .build();
    }
}
