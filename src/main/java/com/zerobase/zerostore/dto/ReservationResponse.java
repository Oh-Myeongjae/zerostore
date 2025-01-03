package com.zerobase.zerostore.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {
    private Long id;
    private String storeName;
    private String userName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime reservationTime;
    private String status;
}
