package com.rvr.swiss.elk.configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;

import com.rvr.swiss.elk.dto.LogEventDTO;
import com.rvr.swiss.elk.dto.LogTestDTO;
import com.rvr.swiss.elk.handlers.ServerResponseErrorHandler;
import com.rvr.swiss.elk.integration.ElasticsearchClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.ObjectToStringHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Spring context {@link Configuration} class to set up properties for rest integrations
 */
@Configuration
public class RestConfiguration {

    private static final Logger LOG =  LoggerFactory.getLogger(ElasticsearchClientImpl.class);

    @Bean(name = "restTemplate")
    public RestTemplate restTemplate(@Autowired ObjectMapper mapper) {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(mapper);

        RestTemplate restTemplate = new RestTemplate(Arrays.asList(
                new StringHttpMessageConverter(),
                new ObjectToStringHttpMessageConverter(new DefaultFormattingConversionService()),
                jsonConverter));

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        BufferingClientHttpRequestFactory bufferingClientHttpRequestFactory =
                new BufferingClientHttpRequestFactory(requestFactory);
        requestFactory.setOutputStreaming(false);
        restTemplate.setRequestFactory(bufferingClientHttpRequestFactory);
        restTemplate.setErrorHandler(new ServerResponseErrorHandler(mapper));
        return restTemplate;
    }
}
