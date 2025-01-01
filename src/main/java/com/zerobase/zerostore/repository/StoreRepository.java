package com.zerobase.zerostore.repository;

import com.zerobase.zerostore.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByNameAndOwnerId(String name, Long ownerId);

    Optional<Store> findByIdAndOwnerId(Long id, Long ownerId);

    List<Store> findAllByOwnerId(Long ownerId); // 특정 소유자의 모든 상점 조회
}

