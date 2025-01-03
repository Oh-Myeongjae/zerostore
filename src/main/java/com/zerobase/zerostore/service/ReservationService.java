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
        // 예약 시간 유효성 검증
        if (request.getReservationTime().getMinute() != 0) {
            throw new CustomException(INVALID_RESERVATION_TIME);
        }

        // 상점 존재 확인
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new CustomException(STORE_NOT_FOUND));

        // 예약 생성
        Reservation reservation = Reservation.builder()
                .user(user)
                .store(store)
                .reservationTime(request.getReservationTime())
                .status(ReservationStatus.PENDING)
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
    public List<ReservationResponse> getReservationsByStore(Long storeId) {
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
    public void updateReservationStatus(Long reservationId, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(RESERVATION_NOT_FOUND));

        reservation.setStatus(status);
    }

    // 예약 사용 처리
    @Transactional
    public void markReservationAsUsed(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(RESERVATION_NOT_FOUND));

        reservation.setUsed(true);
    }
}

