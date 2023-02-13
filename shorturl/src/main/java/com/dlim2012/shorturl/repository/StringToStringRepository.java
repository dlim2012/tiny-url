package com.dlim2012.shorturl.repository;

import com.dlim2012.shorturl.entity.StringToStringEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StringToStringRepository extends CassandraRepository<StringToStringEntity, String> {

}