package com.rvr.swiss.elk.integration;

import com.rvr.swiss.elk.dto.LogEventDTO;

public interface ElasticsearchClient {
    void send(LogEventDTO event);
}
