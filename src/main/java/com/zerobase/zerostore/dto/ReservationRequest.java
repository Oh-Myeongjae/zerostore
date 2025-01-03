package com.zerobase.zerostore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequest {
    private Long storeId; // 상점 ID
    private LocalDateTime reservationTime; // 예약 시간 (정각 단위)
}
