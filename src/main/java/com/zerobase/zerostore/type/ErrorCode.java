package com.zerobase.zerostore.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(500,"내부 서버 오류가 발생했습니다."),
    INVALID_REQUEST(400,"잘못된 요청입니다."),
    USER_ALREADY_REGISTERED(409 ,"이미 회원가입이 완료된 전화번호 입니다."),
    USER_NOT_FOUND(400,"회원가입된 사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(400,"잘못된 비밀번호를 입력했습니다."),
    INVALID_ROLE(403, "PARTNER 권한이 필요합니다."),
    DUPLICATE_STORE_NAME(409, "이미 동일한 상호명이 등록되어 있습니다."),
    STORE_NOT_FOUND(404, "상점을 찾을 수 없습니다.");

    private final int status;
    private final String description;
}
