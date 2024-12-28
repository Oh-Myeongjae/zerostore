package com.zerobase.zerostore.exception;

import com.zerobase.zerostore.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CustomException extends RuntimeException {
    private final int status;
    private final String errorMessage;

    public CustomException(ErrorCode errorCode){
        this.status = errorCode.getStatus();
        this.errorMessage = errorCode.getDescription();
    }

}

