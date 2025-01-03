package com.zerobase.zerostore.repository;

import com.zerobase.zerostore.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByUserId(Long userId); // 특정 사용자의 예약 목록
    List<Reservation> findAllByStoreId(Long storeId); // 특정 상점의 예약 목록
}

