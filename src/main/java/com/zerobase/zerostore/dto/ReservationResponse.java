package com.zerobase.zerostore.dto;

import com.zerobase.zerostore.type.ReservationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {
    private Long id;
    private String storeName;
    private String userName;
    private LocalDateTime reservationTime;
    private ReservationStatus status;
}
