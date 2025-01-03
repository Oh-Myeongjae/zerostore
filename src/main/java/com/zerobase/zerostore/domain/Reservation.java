package com.zerobase.zerostore.domain;

import com.zerobase.zerostore.type.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

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
            throw new IllegalStateException("승인된 예약은 대기 상태로 변경할 수 없습니다.");
        }
        this.status = status;
    }

    // 사용 처리 상태 업데이트
    public void setUsed(boolean used) {
        if (used && !Objects.equals(this.status, ReservationStatus.APPROVED.getStatus())) {
            throw new IllegalStateException("승인된 예약만 사용 처리할 수 있습니다.");
        }
        if (this.used && used) {
            throw new IllegalStateException("이미 사용 처리된 예약입니다.");
        }
        this.used = used;
    }
}

