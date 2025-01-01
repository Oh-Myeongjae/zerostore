package com.zerobase.zerostore.service;

import com.zerobase.zerostore.domain.Store;
import com.zerobase.zerostore.domain.User;
import com.zerobase.zerostore.dto.StoreRequestDto;
import com.zerobase.zerostore.dto.StoreResponseDto;
import com.zerobase.zerostore.dto.StoreUpdateRequestDto;
import com.zerobase.zerostore.exception.CustomException;
import com.zerobase.zerostore.repository.StoreRepository;
import com.zerobase.zerostore.repository.UserRepository;
import com.zerobase.zerostore.type.ErrorCode;
import com.zerobase.zerostore.type.Role;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StoreService {
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public void registerStore(String useNumber, StoreRequestDto storeRequest) {
        //PARTNER권한을 가진 사장님 객체 가져오기
        User user = userRepository.findByPhoneNumber(useNumber)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        // PARTNER 권한 검사
        if (user.getRole() != Role.PARTNER) {
            throw new CustomException(ErrorCode.INVALID_ROLE);
        }

        // 동일한 상호명의 상점이 이미 등록했는지 체크
        if (storeRepository.findByNameAndOwnerId(storeRequest.getName(), user.getId()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_STORE_NAME);
        }

        // Store 엔티티 생성 및 저장
        Store store = Store.builder()
                .name(storeRequest.getName())
                .location(storeRequest.getLocation())
                .description(storeRequest.getDescription())
                .owner(user)
                .build();

        storeRepository.save(store);
    }

    @Transactional
    public void updateStore(Long storeId, User owner, StoreUpdateRequestDto request) {
        // 소유자가 해당 상점을 소유하는지 확인
        Store store = storeRepository.findByIdAndOwnerId(storeId, owner.getId())
                .orElseThrow(() -> new CustomException( ErrorCode.STORE_NOT_FOUND));

        // 상점 정보 수정
        store = Store.builder()
                .id(store.getId()) // 기존 ID 유지
                .name(request.getName())
                .location(request.getLocation())
                .description(request.getDescription())
                .owner(store.getOwner()) // 소유자 유지
                .build();

       storeRepository.save(store);
    }

    @Transactional
    public void deleteStore(Long storeId, User owner) {
        // 소유자가 해당 상점을 소유하는지 확인
        Store store = storeRepository.findByIdAndOwnerId(storeId, owner.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        // 상점 삭제
        storeRepository.delete(store);
    }

    // 전체 상점 리스트 조회
    public List<StoreResponseDto> getAllStores() {
        return storeRepository.findAll().stream()
                .map(StoreResponseDto::entityToDto)
                .collect(Collectors.toList());
    }

    // 특정 상점 정보 조회
    public StoreResponseDto getStoreById(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        return StoreResponseDto.entityToDto(store);
    }

    //파트너의 모든 상점 조회
    public List<StoreResponseDto> getStoresByOwner(User owner) {
        if (owner.getRole() != Role.PARTNER) {
            throw new CustomException(ErrorCode.INVALID_ROLE);
        }

        return storeRepository.findAllByOwnerId(owner.getId()).stream()
                .map(StoreResponseDto::entityToDto)
                .collect(Collectors.toList());
    }
}

