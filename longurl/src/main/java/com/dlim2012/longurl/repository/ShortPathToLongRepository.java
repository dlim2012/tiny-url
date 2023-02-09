package com.dlim2012.longurl.repository;

import com.dlim2012.longurl.entity.ShortPathToLong;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortPathToLongRepository extends CrudRepository<ShortPathToLong, String> {

    Optional<ShortPathToLong> findByShortURLPath(String shortPath);
}
