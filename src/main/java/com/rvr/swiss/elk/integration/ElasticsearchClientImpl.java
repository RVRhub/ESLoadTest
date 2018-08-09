package com.rvr.swiss.elk.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rvr.swiss.elk.dto.LogEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.UUID;

@Component
@Slf4j
public class ElasticsearchClientImpl implements ElasticsearchClient {

    private static final Logger LOG =  LoggerFactory.getLogger(ElasticsearchClientImpl.class);
    private static final String INDEX_URL = "/{indexName}/{type}/{id}";
    private static final String UPDATE_PATH = "/_update";

    private final RestTemplate restTemplate;

    @Value("${elasticsearch.url}")
    private String elasticsearchUrl;

    @Value("${elasticsearch.index}")
    private String indexName;

    @Value("${elasticsearch.statisticsIndex}")
    private String statisticsIndex;

    @Value("${elasticsearch.type}")
    private String type;

    @Autowired
    public ElasticsearchClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    ObjectMapper mapper;

    @Override
    public void send(LogEventDTO event) {
        String eventID = UUID.randomUUID().toString();
        try {
            HttpEntity<LogEventDTO> eventEntity = new HttpEntity<>(event);
            restTemplate.exchange(elasticsearchUrl + INDEX_URL, HttpMethod.PUT, eventEntity,
                    String.class, indexName, type, eventID);


            LOG.trace("Sent event {} to Elasticsearch.", eventID);
        } catch (Exception e) {
            LOG.error("Error sending event {} to Elasticsearch", eventID, e);
            throw new IllegalStateException(e);
        }
    }

}
