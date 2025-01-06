package com.zerobase.zerostore.service;

import com.zerobase.zerostore.domain.Store;
import com.zerobase.zerostore.domain.User;
import com.zerobase.zerostore.dto.StoreRequest;
import com.zerobase.zerostore.dto.StoreResponse;
import com.zerobase.zerostore.dto.StoreUpdateRequest;
import com.zerobase.zerostore.exception.CustomException;
import com.zerobase.zerostore.repository.StoreRepository;
import com.zerobase.zerostore.repository.UserRepository;
import com.zerobase.zerostore.type.ErrorCode;
import com.zerobase.zerostore.type.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    /**
     * 새로운 상점을 등록하는 메서드입니다.
     * 파트너 권한을 가진 사용자가 상점을 등록할 수 있도록 합니다.
     * 동일한 상호명으로 이미 등록된 상점이 있을 경우 예외를 발생시킵니다.
     *
     * @param useNumber 파트너의 전화번호
     * @param storeRequest 상점 등록 요청 정보
     * @throws CustomException 사용자 정보가 없거나, 권한이 없거나, 상호명이 중복되는 경우 예외를 발생시킴
     */
    @Transactional
    public void registerStore(String useNumber, StoreRequest storeRequest) {
        // PARTNER 권한을 가진 사용자를 조회
        User user = userRepository.findByPhoneNumber(useNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // PARTNER 권한 확인
        if (user.getRole() != Role.PARTNER) {
            throw new CustomException(ErrorCode.INVALID_ROLE);
        }

        // 동일한 상호명이 이미 등록된 상점이 있는지 확인
        if (storeRepository.findByNameAndOwnerId(storeRequest.getName(), user.getId()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_STORE_NAME);
        }

        // 새로운 상점 엔티티 생성
        Store store = Store.builder()
                .name(storeRequest.getName())
                .location(storeRequest.getLocation())
                .description(storeRequest.getDescription())
                .owner(user)
                .build();

        // 상점 정보 저장
        storeRepository.save(store);
    }

    /**
     * 특정 상점의 정보를 수정하는 메서드입니다.
     * 상점의 소유자가 요청한 경우에만 상점 정보를 수정할 수 있습니다.
     *
     * @param storeId 수정할 상점의 ID
     * @param owner 상점의 소유자
     * @param request 상점 수정 요청 정보
     * @throws CustomException 상점이 존재하지 않거나, 소유자가 아닌 경우 예외를 발생시킴
     */
    @Transactional
    public void updateStore(Long storeId, User owner, StoreUpdateRequest request) {
        // 소유자가 해당 상점을 소유하는지 확인
        Store store = storeRepository.findByIdAndOwnerId(storeId, owner.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        // 상점 정보 수정
        store = Store.builder()
                .id(store.getId()) // 기존 ID 유지
                .name(request.getName())
                .location(request.getLocation())
                .description(request.getDescription())
                .owner(store.getOwner()) // 소유자 유지
                .build();

        // 수정된 상점 정보 저장
        storeRepository.save(store);
    }

    /**
     * 특정 상점을 삭제하는 메서드입니다.
     * 상점 삭제는 해당 상점의 소유자만 수행할 수 있습니다.
     *
     * @param storeId 삭제할 상점의 ID
     * @param owner 상점의 소유자
     * @throws CustomException 상점이 존재하지 않거나, 소유자가 아닌 경우 예외를 발생시킴
     */
    @Transactional
    public void deleteStore(Long storeId, User owner) {
        // 소유자가 해당 상점을 소유하는지 확인
        Store store = storeRepository.findByIdAndOwnerId(storeId, owner.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        // 상점 삭제
        storeRepository.delete(store);
    }

    /**
     * 등록된 모든 상점의 정보를 조회하는 메서드입니다.
     *
     * @return 모든 상점 정보를 조회하여 응답
     */
    public List<StoreResponse> getAllStores() {
        return storeRepository.findAll().stream()
                .map(StoreResponse::entityToDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 상점의 정보를 조회하는 메서드입니다.
     *
     * @param storeId 조회할 상점의 ID
     * @return 조회된 상점 정보
     * @throws CustomException 상점이 존재하지 않으면 예외를 발생시킴
     */
    public StoreResponse getStoreById(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        return StoreResponse.entityToDto(store);
    }

    /**
     * 파트너가 소유한 모든 상점 정보를 조회하는 메서드입니다.
     *
     * @param owner 상점의 소유자 (파트너)
     * @return 소유한 모든 상점 정보를 조회하여 응답
     * @throws CustomException 파트너 권한이 없는 경우 예외를 발생시킴
     */
    public List<StoreResponse> getStoresByOwner(User owner) {
        if (owner.getRole() != Role.PARTNER) {
            throw new CustomException(ErrorCode.INVALID_ROLE);
        }

        return storeRepository.findAllByOwnerId(owner.getId()).stream()
                .map(StoreResponse::entityToDto)
                .collect(Collectors.toList());
    }
}