package com.zerobase.zerostore.service;

import com.zerobase.zerostore.domain.Store;
import com.zerobase.zerostore.domain.User;
import com.zerobase.zerostore.dto.StoreRequestDto;
import com.zerobase.zerostore.exception.CustomException;
import com.zerobase.zerostore.repository.StoreRepository;
import com.zerobase.zerostore.repository.UserRepository;
import com.zerobase.zerostore.type.ErrorCode;
import com.zerobase.zerostore.type.Role;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if (storeRepository.findByNameAndOwner(storeRequest.getName(), user).isPresent()) {
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
}

