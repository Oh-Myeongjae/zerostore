package com.zerobase.zerostore.repository;

import com.zerobase.zerostore.domain.Store;
import com.zerobase.zerostore.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByNameAndOwner(String name, User user);
}

