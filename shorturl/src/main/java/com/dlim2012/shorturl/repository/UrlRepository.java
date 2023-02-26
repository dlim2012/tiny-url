package com.dlim2012.shorturl.repository;

import com.dlim2012.shorturl.entity.UrlEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends CassandraRepository<UrlEntity, String> {

//    Optional<UrlEntity> findByKey(String key);

    Optional<UrlEntity> findByKeyAndQueryName(String key, String username);

    @Query("Update tiny_url SET is_active=?2 WHERE key=?0 and query_name=?1" )
    Integer updateIsActive(String key, String queryName, Boolean isActive);

    @Query("Update tiny_url SET value=?2 WHERE key=?0 and query_name=?1" )
    Integer updateValue(String key, String queryName, String value);

    @Query("Update tiny_url SET value=?2, text=?3 WHERE key=?0 and query_name=?1" )
    Integer updateValueAndText(String key, String queryName, String value, String text);

    @Query("select ttl (value) from tiny_url where key=?0 and query_name=?1")
    Integer getTtl(String key, String queryName);
}