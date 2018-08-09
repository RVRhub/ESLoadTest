package com.rvr.swiss.elk.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;


import java.io.IOException;
import java.nio.charset.Charset;
import java.rmi.ServerException;

public class ServerResponseErrorHandler implements ResponseErrorHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ServerResponseErrorHandler.class);

    private final ObjectMapper objectMapper;

    public ServerResponseErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        String body = IOUtils.toString(response.getBody(), Charset.defaultCharset());
        LOG.error("Response error: {} {}", response.getStatusCode(), response.getStatusText());
        ServerException restException = null;
        if (StringUtils.isNotEmpty(body)) {
            restException = objectMapper
                    .readValue(body, ServerException.class);
        }
        throw new RestClientException(restException != null ? restException.getMessage() : "");
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return isError(response.getStatusCode());
    }

    private static boolean isError(HttpStatus status) {
        HttpStatus.Series series = status.series();
        return (HttpStatus.Series.CLIENT_ERROR.equals(series)
                || HttpStatus.Series.SERVER_ERROR.equals(series));
    }
}
