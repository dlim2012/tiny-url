package com.dlim2012.appuser.repository;

import com.dlim2012.appuser.entity.UrlEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlEntity, String> {

    @Query(
            value = "SELECT * from app_user a left join url s on a.id = s.user_id " +
                    "where a.id = ?1 and s.long_url = ?2 and s.is_private = ?3 FOR UPDATE",
            nativeQuery = true
    )
    Optional<UrlEntity> findByUserIdAndLongUrlAndIsPrivateForUpdate(
            int userId, String longUrl, boolean isPrivate
    );

    @Query(
            value = "SELECT * from app_user a left join url s on a.id = s.user_id " +
                    "where a.id = ?1 and s.long_url = ?2 and s.is_private = ?3",
            nativeQuery = true
    )
    Optional<UrlEntity> findByUserIdAndLongUrlAndIsPrivate(
            int userId, String longUrl, boolean isPrivate
    );

    @Query(
            value = "SELECT * from app_user a left join url s on a.id = s.user_id " +
                    "where a.id = ?1 and s.short_url_path = ?2 and s.is_private = ?3",
            nativeQuery = true
    )
    Optional<UrlEntity> findByUserIdAndShortUrlPathAndIsPrivate(
            int userId, String shortUrlPath, boolean isPrivate
    );

    @Query(
            value = "SELECT * from app_user a left join url s on a.id = s.user_id " +
                    "where a.id = ?1 and s.short_url_path = ?2",
            nativeQuery = true
    )
    Optional<UrlEntity> findByUserIdAndShortUrlPath(
            int userId, String shortUrlPath
    );

    @Query(
            value = "SELECT * from app_user a left join url s on a.id = s.user_id " +
                    "where a.id = ?1 and s.is_active = ?2",
            nativeQuery = true
    )
    List<UrlEntity> findByUserIdAndIsActive(
            int userId, boolean isActive
    );

    @Query(
            value = "SELECT * from app_user a left join url s on a.id = s.user_id " +
                    "where a.id = ?1 and s.is_active = ?2 ORDER BY s.url_created_at",
            nativeQuery = true
    )
    List<UrlEntity> findByUserIdAndIsActiveOrderByCreatedAt(
            int userId, boolean isActive
    );

    @Query(
            value = "SELECT * from app_user a left join url s on a.id = s.user_id " +
                    "where a.id = ?1 FOR UPDATE",
            nativeQuery = true
    )
    List<UrlEntity> findByUserId(
            int userId
    );

    @Query(
            value = "SELECT * from app_user a left join url s on a.id = s.user_id " +
                    "where a.id = ?1 ORDER BY s.url_created_at",
            nativeQuery = true
    )
    List<UrlEntity> findByUserIdOrderByCreatedAt(
            int userId
    );


    @Query(
            value = "SELECT * from url s where s.short_url_path = ?1 FOR UPDATE",
            nativeQuery = true
    )
    Optional<UrlEntity> findByShortUrlPathForUpdate(
            String shortUrlPath
    );


    @Query(
            value = "SELECT * from url s where s.short_url_path = ?1",
            nativeQuery = true
    )
    Optional<UrlEntity> findByShortUrlPath(
            String shortUrlPath
    );

    Integer deleteByShortUrlPath(
            String shortUrlPath
    );


    @Query(
            value = "SELECT * FROM url s where s.long_url = ?1",
            nativeQuery = true
    )
    List<UrlEntity> findByLongUrl(String longUrl);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    @Query(
            value = "SELECT s FROM UrlEntity s join s.appUser a " +
                    "WHERE a.email = ?1 and s.longUrl = ?2 and s.isPrivate = ?3"
    )
    Optional<UrlEntity> findByUserEmailAndLongUrlAndIsPrivateForUpdate(
            String userEmail, String longUrl, Boolean isPrivate
    );

}
