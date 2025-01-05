package com.zerobase.zerostore.service;

import com.zerobase.zerostore.domain.Reservation;
import com.zerobase.zerostore.domain.Store;
import com.zerobase.zerostore.domain.User;
import com.zerobase.zerostore.dto.ReservationRequest;
import com.zerobase.zerostore.dto.ReservationResponse;
import com.zerobase.zerostore.exception.CustomException;
import com.zerobase.zerostore.repository.ReservationRepository;
import com.zerobase.zerostore.repository.StoreRepository;
import com.zerobase.zerostore.type.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.zerobase.zerostore.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;

    //예약 생성
    @Transactional
    public ReservationResponse createReservation(User user, ReservationRequest request) {
        LocalDateTime reservationTime = request.getReservationTime();

        // 1. 과거 시간에 대한 예약 방지
        if (reservationTime.isBefore(LocalDateTime.now())) {
            throw new CustomException(RESERVATION_IN_PAST);
        }

        // 2. 예약 시간 유효성 검증 (30분 단위만 가능)
        int minute = reservationTime.getMinute();
        if (minute != 0 && minute != 30) {
            throw new CustomException(INVALID_RESERVATION_TIME);
        }

        // 상점 존재 확인
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new CustomException(STORE_NOT_FOUND));

        // 예약 생성
        Reservation reservation = Reservation.builder()
                .user(user)
                .store(store)
                .reservationTime(reservationTime)
                .status(ReservationStatus.PENDING.getStatus())
                .used(false)
                .build();

        reservationRepository.save(reservation);

        return new ReservationResponse(
                reservation.getId(),
                store.getName(),
                user.getName(),
                reservation.getReservationTime(),
                reservation.getStatus()
        );
    }

    // 예약 목록 조회 (사용자)
    public List<ReservationResponse> getReservationsByUser(User user) {
        return reservationRepository.findAllByUserId(user.getId()).stream()
                .map(reservation -> new ReservationResponse(
                        reservation.getId(),
                        reservation.getStore().getName(),
                        reservation.getUser().getName(),
                        reservation.getReservationTime(),
                        reservation.getStatus()
                ))
                .collect(Collectors.toList());
    }

    // 예약 목록 조회 (파트너)
    public List<ReservationResponse> getReservationsByStore(Long storeId, User user) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(STORE_NOT_FOUND));

        // 상점 소유자 검증
        if (!store.getOwner().getId().equals(user.getId())) {
            throw new CustomException(ACCESS_DENIED);
        }

        return reservationRepository.findAllByStoreId(storeId).stream()
                .map(reservation -> new ReservationResponse(
                        reservation.getId(),
                        reservation.getStore().getName(),
                        reservation.getUser().getName(),
                        reservation.getReservationTime(),
                        reservation.getStatus()
                ))
                .collect(Collectors.toList());
    }

    // 예약 상태 변경
    @Transactional
    public void updateReservationStatus(Long reservationId, String status, User user) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(RESERVATION_NOT_FOUND));

        // 상점 소유자 검증
        if (!reservation.getStore().getOwner().getId().equals(user.getId())) {
            throw new CustomException(ACCESS_DENIED);
        }

        reservation.setStatus(status);
    }

    // 예약 사용 처리
    @Transactional
    public void markReservationAsUsed(Long reservationId, User user) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(RESERVATION_NOT_FOUND));

        // 상점 소유자 검증
        if (!reservation.getStore().getOwner().getId().equals(user.getId())) {
            throw new CustomException(ACCESS_DENIED);
        }

        // 현재 시간과 예약 시간 비교
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reservationTime = reservation.getReservationTime();

        // 예약 시간이 현재 시간 기준 10분 전 이후인지 확인
        if (now.isBefore(reservationTime.minusMinutes(10))) {
            throw new CustomException(INVALID_INPUT_VALUE);
        }

        reservation.setUsed(true);
    }
}

