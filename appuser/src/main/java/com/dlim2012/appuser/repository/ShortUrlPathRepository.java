package com.dlim2012.appuser.repository;

import com.dlim2012.appuser.entity.ShortUrlPathEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ShortUrlPathRepository extends JpaRepository<ShortUrlPathEntity, Integer> {

    @Query(
            value = "SELECT * from app_user a left join short_url_path s on a.id = s.user_id " +
                    "where a.id = ?1 and s.short_url_path = ?2 and s.is_private = ?3",
            nativeQuery = true
    )
    Optional<ShortUrlPathEntity> findByUserIdAndShortUrlPathAndIsPrivate(
            int userId, String shortUrlPath, boolean isPrivate
    );

    @Query(
            value = "SELECT * from app_user a left join short_url_path s on a.id = s.user_id " +
                    "where a.id = ?1 and s.is_active = ?2",
            nativeQuery = true
    )
    List<ShortUrlPathEntity> findByUserIdAndIsActive(
            int userId, boolean isActive
    );

    @Query(
            value = "SELECT * from app_user a left join short_url_path s on a.id = s.user_id " +
                    "where a.id = ?1",
            nativeQuery = true
    )
    List<ShortUrlPathEntity> findByUserId(
            int userId
    );


    @Query(
            value = "SELECT * from short_url_path s where s.short_url_path = ?1",
            nativeQuery = true
    )
    Optional<ShortUrlPathEntity> findByShortUrlPath(
            String shortUrlPath
    );


}
