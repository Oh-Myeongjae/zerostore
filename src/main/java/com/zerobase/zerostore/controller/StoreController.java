package com.zerobase.zerostore.controller;

import com.zerobase.zerostore.dto.CommonResponseUtil;
import com.zerobase.zerostore.dto.StoreRequest;
import com.zerobase.zerostore.dto.StoreResponse;
import com.zerobase.zerostore.dto.StoreUpdateRequest;
import com.zerobase.zerostore.security.UserDetailsImpl;
import com.zerobase.zerostore.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Store", description = "상점 관리 API")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/store")
public class StoreController {

    private final StoreService storeService;

    /**
     * 파트너 사용자가 새로운 상점을 등록하는 메서드입니다.
     * 사용자는 로그인 후 상점 정보를 입력하여 상점을 등록할 수 있습니다.
     *
     * @param storeRequest 상점 등록에 필요한 정보 (상점 이름, 위치 등)
     * @param user 인증된 사용자 정보 (파트너 사용자)
     * @return 상점 등록 성공 메시지를 포함한 응답
     */
    @Operation(summary = "상점 등록", description = "파트너 사용자가 새로운 상점을 등록합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/register")
    public ResponseEntity<?> registerStore(
            @RequestBody @Valid StoreRequest storeRequest,
            @AuthenticationPrincipal UserDetailsImpl user
    ) {
        if (user == null) {
            return ResponseEntity.ok(CommonResponseUtil.error(403, "상점등록을 위한 권한이 없습니다."));
        }
        storeService.registerStore(user.getUseNumber(), storeRequest);
        return ResponseEntity.ok(CommonResponseUtil.success("상점등록 성공"));
    }

    /**
     * 파트너 사용자가 특정 상점의 정보를 수정하는 메서드입니다.
     * 사용자는 자신이 소유한 상점의 정보를 수정할 수 있습니다.
     *
     * @param storeId 수정할 상점의 ID
     * @param user 인증된 사용자 정보 (파트너 사용자)
     * @param request 상점 수정에 필요한 정보 (상점 이름, 위치 등)
     * @return 상점 수정 성공 메시지를 포함한 응답
     */
    @Operation(summary = "상점 정보 수정", description = "파트너 사용자가 특정 상점 정보를 수정합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{storeId}")
    public ResponseEntity<CommonResponseUtil<?>> updateStore(
            @PathVariable Long storeId,
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestBody @Valid StoreUpdateRequest request
    ) {
        if (user == null) {
            return ResponseEntity.ok(CommonResponseUtil.error(403, "상점정보 수정을 위한 권한이 없습니다."));
        }
        storeService.updateStore(storeId, user.getUser(), request);
        return ResponseEntity.ok(CommonResponseUtil.success("상점 수정 성공"));
    }

    /**
     * 파트너 사용자가 특정 상점을 삭제하는 메서드입니다.
     * 사용자는 자신이 소유한 상점을 삭제할 수 있습니다.
     *
     * @param storeId 삭제할 상점의 ID
     * @param user 인증된 사용자 정보 (파트너 사용자)
     * @return 상점 삭제 성공 메시지를 포함한 응답
     */
    @Operation(summary = "상점 삭제", description = "파트너 사용자가 특정 상점을 삭제합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{storeId}")
    public ResponseEntity<CommonResponseUtil<?>> deleteStore(
            @PathVariable Long storeId,
            @AuthenticationPrincipal UserDetailsImpl user
    ) {
        if (user == null) {
            return ResponseEntity.ok(CommonResponseUtil.error(403, "상점을 삭제하기 위한 권한이 없습니다."));
        }
        storeService.deleteStore(storeId, user.getUser());
        return ResponseEntity.ok(CommonResponseUtil.success("상점 삭제 성공"));
    }

    /**
     * 등록된 모든 상점 정보를 조회하는 메서드입니다.
     * 모든 상점의 정보를 조회하고, 반환합니다.
     *
     * @return 모든 상점의 리스트
     */
    @Operation(summary = "전체 상점 조회", description = "등록된 모든 상점 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<CommonResponseUtil<?>> getAllStores() {
        List<StoreResponse> stores = storeService.getAllStores();
        return ResponseEntity.ok(CommonResponseUtil.success("전체 상점 조회 성공", stores));
    }

    /**
     * 상점 ID를 기준으로 특정 상점 정보를 조회하는 메서드입니다.
     * 사용자는 상점 ID를 제공하여 해당 상점의 정보를 조회할 수 있습니다.
     *
     * @param storeId 조회할 상점의 ID
     * @return 특정 상점의 정보
     */
    @Operation(summary = "특정 상점 조회", description = "상점 ID를 기준으로 특정 상점 정보를 조회합니다.")
    @GetMapping("/{storeId}")
    public ResponseEntity<CommonResponseUtil<?>> getStoreById(@PathVariable Long storeId) {
        StoreResponse store = storeService.getStoreById(storeId);
        return ResponseEntity.ok(CommonResponseUtil.success("상점 조회 성공", store));
    }

    /**
     * 로그인된 파트너 사용자가 소유한 모든 상점을 조회하는 메서드입니다.
     * 사용자는 자신의 계정으로 로그인한 후, 소유한 상점 목록을 조회할 수 있습니다.
     *
     * @param user 인증된 파트너 사용자 정보
     * @return 사용자가 소유한 상점 목록
     */
    @Operation(summary = "파트너 상점 조회", description = "로그인된 파트너 사용자가 소유한 모든 상점을 조회합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/owned")
    public ResponseEntity<CommonResponseUtil<?>> getStoresByOwner(@AuthenticationPrincipal UserDetailsImpl user) {
        List<StoreResponse> stores = storeService.getStoresByOwner(user.getUser());
        return ResponseEntity.ok(CommonResponseUtil.success("파트너 상점 조회 성공", stores));
    }
}