package com.zerobase.zerostore.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationStatus {
    PENDING("대기"),
    APPROVED("승인"),
    REJECTED("거절"),
    COMPLETED("사용 완료") ;

    private final String status;
}

