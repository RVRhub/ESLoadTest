package com.rvr.swiss.elk.repository;


import com.rvr.swiss.elk.dto.LogEventDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ESRepository extends ElasticsearchRepository<LogEventDTO, Long> {

}
