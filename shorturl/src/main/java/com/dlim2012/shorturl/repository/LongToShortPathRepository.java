package com.dlim2012.shorturl.repository;

import com.dlim2012.shorturl.entity.LongToShortPath;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface LongToShortPathRepository extends CrudRepository<LongToShortPath, String> {

    Optional<LongToShortPath> findByLongURL(String longURL);
}
