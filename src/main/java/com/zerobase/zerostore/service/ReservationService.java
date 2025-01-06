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

    /**
     * 새로운 예약을 생성하는 메서드입니다.
     * - 예약 시간은 과거 시간일 수 없으며, 30분 단위로만 예약할 수 있습니다.
     * - 예약하려는 상점이 존재하는지 확인합니다.
     *
     * @param user 예약을 생성할 사용자
     * @param request 예약 요청 정보
     * @return 생성된 예약의 응답 정보
     * @throws CustomException 과거 예약 시간, 30분 단위가 아닌 예약 시간, 상점이 존재하지 않는 경우 예외 발생
     */
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
                .status(ReservationStatus.PENDING.getStatus())  // 기본 상태: 대기 중
                .used(false)  // 기본적으로 사용되지 않은 예약
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

    /**
     * 사용자가 자신의 예약 목록을 조회하는 메서드입니다.
     *
     * @param user 예약 목록을 조회할 사용자
     * @return 사용자의 예약 목록
     */
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

    /**
     * 파트너(상점 소유자)가 자신의 상점에 대한 예약 목록을 조회하는 메서드입니다.
     *
     * @param storeId 조회할 상점의 ID
     * @param user 예약 목록을 조회할 파트너
     * @return 상점에 대한 예약 목록
     * @throws CustomException 상점이 존재하지 않거나, 사용자가 상점 소유자가 아닌 경우 예외 발생
     */
    public List<ReservationResponse> getReservationsByStore(Long storeId, User user) {
        // 상점 존재 여부 확인
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

    /**
     * 예약 상태를 변경하는 메서드입니다.
     * - 예약 상태는 파트너(상점 소유자)만 변경할 수 있습니다.
     *
     * @param reservationId 예약 ID
     * @param status 변경할 예약 상태
     * @param user 예약 상태를 변경할 사용자 (파트너)
     * @throws CustomException 예약이 존재하지 않거나, 사용자가 상점 소유자가 아닌 경우 예외 발생
     */
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

    /**
     * 예약을 사용 처리하는 메서드입니다.
     * - 상점 소유자만 사용 처리할 수 있으며, 예약 시간과 현재 시간의 차이를 확인하여 유효성 검사를 수행합니다.
     *
     * @param reservationId 사용 처리할 예약 ID
     * @param user 예약을 사용 처리할 사용자 (상점 소유자)
     * @throws CustomException 예약이 존재하지 않거나, 사용자가 상점 소유자가 아닌 경우,
     *                         예약 시간이 현재 시간 기준 10분 전 이후인 경우 예외 발생
     */
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