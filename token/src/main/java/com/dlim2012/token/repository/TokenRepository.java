package com.dlim2012.token.repository;

import com.dlim2012.token.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Optional;


@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

//    @Query(
//            value = "Delete * from Key k where k.createdAt < DATE_SUB(CURDATE(), INTERVAL 1 YEAR)",
//            nativeQuery = true
//    )
//    void deleteExpired();

    @Query(
            value = "SELECT seed FROM token ORDER BY created_at DESC LIMIT 0, 1",
            nativeQuery = true
    )
    Optional<Integer> getLastSeed();
}
