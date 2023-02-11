package com.dlim2012.token.repository;

import com.dlim2012.token.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query(
            value = "SELECT seed FROM token ORDER BY created_at DESC LIMIT 0, 1",
            nativeQuery = true
    )
    Optional<Integer> getLastSeed();
}


