package com.zerobase.zerostore.domain;

import com.zerobase.zerostore.exception.CustomException;
import com.zerobase.zerostore.type.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.zerobase.zerostore.type.ErrorCode.*;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false)
    private LocalDateTime reservationTime;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private boolean used;

    // 예약 상태 업데이트
    public void setStatus(String status) {
        if (Objects.equals(this.status, ReservationStatus.APPROVED.getStatus()) && Objects.equals(status, ReservationStatus.PENDING.getStatus())) {
            throw new CustomException(RESERVATION_STATE_CONFLICT);
        }
        this.status = status;
    }

    // 사용 처리 상태 업데이트
    public void setUsed(boolean used) {
        if (this.used) {
            throw new CustomException(RESERVATION_ALREADY_USED);
        }
        if (!Objects.equals(this.status, ReservationStatus.APPROVED.getStatus())) {
            throw new CustomException(RESERVATION_NOT_APPROVED);
        }
        this.used = used;
        this.status = ReservationStatus.COMPLETED.getStatus();
    }
}

