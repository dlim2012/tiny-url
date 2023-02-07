package com.dlim2012.url.repository;

import com.dlim2012.url.entity.URL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface URLRepository extends JpaRepository<URL, Long> {

    @Query(
            value = "Select * from url u WHERE u.long_url = ?1",
            nativeQuery = true
    )
    Optional<URL> findByLongURL(String longURL);


    @Query(
            value = "Select * from url u WHERE u.short_url = ?1",
            nativeQuery = true
    )
    Optional<URL> findByShortURL(String shortURL);
}
