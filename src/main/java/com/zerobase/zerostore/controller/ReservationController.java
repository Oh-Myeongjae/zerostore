package com.zerobase.zerostore.controller;

import com.zerobase.zerostore.dto.CommonResponseUtil;
import com.zerobase.zerostore.dto.ReservationRequest;
import com.zerobase.zerostore.dto.ReservationResponse;
import com.zerobase.zerostore.security.UserDetailsImpl;
import com.zerobase.zerostore.service.ReservationService;
import com.zerobase.zerostore.type.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // 1. 예약 생성
    @PostMapping
    public ResponseEntity<CommonResponseUtil<?>> createReservation(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestBody ReservationRequest request) {
        ReservationResponse reservation = reservationService.createReservation(user.getUser(), request);
        return ResponseEntity.ok(CommonResponseUtil.success("예약 생성 성공", reservation));
    }

    // 2. 사용자 예약 목록 조회
    @GetMapping
    public ResponseEntity<CommonResponseUtil<?>> getUserReservations(
            @AuthenticationPrincipal UserDetailsImpl user) {
        List<ReservationResponse> reservations = reservationService.getReservationsByUser(user.getUser());
        return ResponseEntity.ok(CommonResponseUtil.success("예약 목록 조회 성공", reservations));
    }

    // 3. 특정 상점 예약 목록 조회 (파트너)
    @GetMapping("/store/{storeId}")
    public ResponseEntity<CommonResponseUtil<?>> getStoreReservations(@PathVariable Long storeId) {
        List<ReservationResponse> reservations = reservationService.getReservationsByStore(storeId);
        return ResponseEntity.ok(CommonResponseUtil.success("상점 예약 목록 조회 성공", reservations));
    }

    // 4. 예약 상태 변경 (파트너)
    @PatchMapping("/{reservationId}/status")
    public ResponseEntity<CommonResponseUtil<?>> updateReservationStatus(
            @PathVariable Long reservationId,
            @RequestParam ReservationStatus status) {
        reservationService.updateReservationStatus(reservationId, status);
        return ResponseEntity.ok(CommonResponseUtil.success("예약 상태 변경 성공"));
    }

    // 5. 예약 사용 처리
    @PatchMapping("/{reservationId}/used")
    public ResponseEntity<CommonResponseUtil<?>> markReservationAsUsed(@PathVariable Long reservationId) {
        reservationService.markReservationAsUsed(reservationId);
        return ResponseEntity.ok(CommonResponseUtil.success("예약 사용 처리 성공"));
    }
}

