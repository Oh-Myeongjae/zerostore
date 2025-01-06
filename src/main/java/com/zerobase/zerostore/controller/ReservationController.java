package com.zerobase.zerostore.controller;

import com.zerobase.zerostore.dto.CommonResponseUtil;
import com.zerobase.zerostore.dto.ReservationRequest;
import com.zerobase.zerostore.dto.ReservationResponse;
import com.zerobase.zerostore.security.UserDetailsImpl;
import com.zerobase.zerostore.service.ReservationService;
import com.zerobase.zerostore.type.ReservationStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Reservation", description = "예약 관리 API")
@RestController
@RequestMapping("/api/reservation")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 사용자가 새로운 예약을 생성하는 메서드입니다.
     * 사용자는 예약 정보를 입력하여 새로운 예약을 생성할 수 있습니다.
     *
     * @param user 인증된 사용자 정보
     * @param request 생성할 예약에 대한 요청 정보
     * @return 예약 생성 성공 메시지와 생성된 예약 정보
     */
    @Operation(summary = "예약 생성", description = "사용자가 새로운 예약을 생성합니다.")
    @PostMapping
    public ResponseEntity<CommonResponseUtil<?>> createReservation(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestBody ReservationRequest request) {
        ReservationResponse reservation = reservationService.createReservation(user.getUser(), request);
        return ResponseEntity.ok(CommonResponseUtil.success("예약 생성 성공", reservation));
    }

    /**
     * 사용자가 본인의 예약 목록을 조회하는 메서드입니다.
     * 사용자는 로그인 후 본인이 생성한 모든 예약을 조회할 수 있습니다.
     *
     * @param user 인증된 사용자 정보
     * @return 사용자의 모든 예약 목록
     */
    @Operation(summary = "사용자 예약 목록 조회", description = "현재 사용자의 예약 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<CommonResponseUtil<?>> getUserReservations(
            @AuthenticationPrincipal UserDetailsImpl user) {
        List<ReservationResponse> reservations = reservationService.getReservationsByUser(user.getUser());
        return ResponseEntity.ok(CommonResponseUtil.success("예약 목록 조회 성공", reservations));
    }

    /**
     * 파트너가 소유한 특정 상점의 예약 목록을 조회하는 메서드입니다.
     * 상점 소유자는 해당 상점의 예약 목록을 조회할 수 있습니다.
     *
     * @param storeId 조회할 상점의 ID
     * @param user 인증된 파트너 사용자 정보
     * @return 상점의 예약 목록
     */
    @Operation(summary = "상점 예약 목록 조회", description = "파트너가 소유한 특정 상점의 예약 목록을 조회합니다.")
    @GetMapping("/store/{storeId}")
    public ResponseEntity<CommonResponseUtil<?>> getStoreReservations(
            @PathVariable Long storeId,
            @AuthenticationPrincipal UserDetailsImpl user) {
        List<ReservationResponse> reservations = reservationService.getReservationsByStore(storeId, user.getUser());
        return ResponseEntity.ok(CommonResponseUtil.success("상점 예약 목록 조회 성공", reservations));
    }

    /**
     * 예약의 상태를 변경하는 메서드입니다.
     * 사용자는 예약의 상태를 변경할 수 있으며, 예약 상태는 다양한 상태로 전환 가능합니다.
     *
     * @param reservationId 상태를 변경할 예약의 ID
     * @param status 변경할 예약의 상태
     * @param user 인증된 사용자 정보
     * @return 예약 상태 변경 성공 메시지
     */
    @Operation(summary = "예약 상태 변경", description = "예약의 상태를 변경합니다.")
    @PatchMapping("/{reservationId}")
    public ResponseEntity<CommonResponseUtil<?>> updateReservationStatus(
            @PathVariable Long reservationId,
            @RequestParam ReservationStatus status,
            @AuthenticationPrincipal UserDetailsImpl user) {
        reservationService.updateReservationStatus(reservationId, status.getStatus(), user.getUser());
        return ResponseEntity.ok(CommonResponseUtil.success("예약 상태 변경 성공"));
    }

    /**
     * 예약을 '사용 처리' 상태로 변경하는 메서드입니다.
     * 사용자는 해당 예약이 실제로 사용되었음을 표시할 수 있습니다.
     *
     * @param reservationId 사용 처리할 예약의 ID
     * @param user 인증된 사용자 정보
     * @return 예약 사용 처리 성공 메시지
     */
    @Operation(summary = "예약 사용 처리", description = "예약을 사용 처리합니다.")
    @PatchMapping("/{reservationId}/used")
    public ResponseEntity<CommonResponseUtil<?>> markReservationAsUsed(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal UserDetailsImpl user) {
        reservationService.markReservationAsUsed(reservationId, user.getUser());
        return ResponseEntity.ok(CommonResponseUtil.success("예약 사용 처리 성공"));
    }
}

