package com.rvr.swiss.elk.configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;

import com.rvr.swiss.elk.dto.LogEventDTO;
import com.rvr.swiss.elk.dto.LogTestDTO;
import com.rvr.swiss.elk.handlers.ServerResponseErrorHandler;
import com.rvr.swiss.elk.integration.ElasticsearchClientImpl;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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

    // Determines the timeout in milliseconds until a connection is established.
    private static final int CONNECT_TIMEOUT = 30000;

    // The timeout when requesting a connection from the connection manager.
    private static final int REQUEST_TIMEOUT = 30000;

    // The timeout for waiting for data
    private static final int SOCKET_TIMEOUT = 60000;


    private static final int MAX_TOTAL_CONNECTIONS = 800;
    private static final int MAX_TOTAL_PER_ROUTE_CONNECTIONS = 800;


    @Bean(name = "restTemplate")
    public RestTemplate restTemplate(@Autowired ObjectMapper mapper) {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(mapper);

        RestTemplate restTemplate = new RestTemplate(Arrays.asList(
                new StringHttpMessageConverter(),
                new ObjectToStringHttpMessageConverter(new DefaultFormattingConversionService()),
                jsonConverter));

        HttpComponentsClientHttpRequestFactory factory = clientHttpRequestFactory();

       // SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        BufferingClientHttpRequestFactory bufferingClientHttpRequestFactory =
                new BufferingClientHttpRequestFactory(factory);
        //requestFactory.setOutputStreaming(false);
        restTemplate.setRequestFactory(bufferingClientHttpRequestFactory);
        restTemplate.setErrorHandler(new ServerResponseErrorHandler(mapper));
        return restTemplate;
    }

    private HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(getHttpClient());
        return requestFactory;
    }


    private CloseableHttpClient getHttpClient(){
        return HttpClients.custom()
                .setDefaultRequestConfig(getRequestConfig())
                .setConnectionManager(poolingConnectionManager())
                .setDefaultSocketConfig(getSocketConfig())
                .build();
    }

    private SocketConfig getSocketConfig() {
        return SocketConfig.custom().setSoKeepAlive(true).setTcpNoDelay(true).build();
    }

    private RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                    .setConnectionRequestTimeout(REQUEST_TIMEOUT)
                    .setConnectTimeout(CONNECT_TIMEOUT)
                    .setSocketTimeout(SOCKET_TIMEOUT).build();
    }

    private PoolingHttpClientConnectionManager poolingConnectionManager(){
        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
        poolingConnectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        poolingConnectionManager.setDefaultMaxPerRoute(MAX_TOTAL_PER_ROUTE_CONNECTIONS);
        return poolingConnectionManager;
    }
}
