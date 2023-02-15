package com.dlim2012.shorturl.repository;

import com.dlim2012.shorturl.entity.UrlEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends CassandraRepository<UrlEntity, String> {

//    Optional<UrlEntity> findByKey(String key);

    Optional<UrlEntity> findByKeyAndQueryName(String key, String username);

}