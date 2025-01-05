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
    STORE_NOT_FOUND(404, "상점을 찾을 수 없습니다."),
    INVALID_RESERVATION_TIME(400,"예약은 매 시간 정각 단위로만 가능합니다."),

    RESERVATION_NOT_FOUND(400, "예약을 찾을 수 없습니다."),
    UNAUTHORIZED_ACTION(403, "권한이 없습니다."),
    DUPLICATE_RESERVATION(409, "이미 동일한 시간에 예약이 존재합니다."),

    ACCESS_DENIED(403, "정보를 수정할 권한이 없습니다."),
    INVALID_INPUT_VALUE(422, "예약한 시간 10분 전부터 사용할 수 있습니다"),
    RESERVATION_ALREADY_USED(400,"이미 사용 처리된 예약입니다."),
    RESERVATION_NOT_APPROVED(400,"승인되지 않은 예약은 사용 처리할 수 없습니다."),
    REVIEW_NOT_FOUND(404,"작성한 리뷰를 찾을 수 없습니다."),

    RESERVATION_NOT_USED(400, "상점을 이용하지 않아 리뷰를 작성할 수 없습니다.");

    private final int status;
    private final String description;
}
