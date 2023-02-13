package com.dlim2012.appuser.repository;

import com.dlim2012.appuser.entity.AppUser;
import com.dlim2012.appuser.entity.ShortUrlPathEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ShortUrlPathRepository extends JpaRepository<ShortUrlPathEntity, Integer> {

    @Query(
            value = "SELECT short_url_path from app_user a left join short_url_path s on a.id = s.user_id " +
                    "where a.id = ?1 and s.short_url_path = ?2",
            nativeQuery = true
    )
    Optional<String> findByUserIdAndShortUrlPath(int userId, String shortUrlPath);

}
