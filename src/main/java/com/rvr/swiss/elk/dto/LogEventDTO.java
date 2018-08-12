package com.rvr.swiss.elk.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.rvr.swiss.elk.dto.LoggingConst.*;

@Document(indexName = "test_roman")
@Data
public class LogEventDTO {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Id
    private Long id;

    private String eventSubTypeID;
    private String eventTypeID;
    private String node;
    private String level;
    private String messageText;
    private String loggingTime = LocalDateTime.now().withHour(10).format(DateTimeFormatter.ISO_DATE_TIME);
    private String exceptionMessage;
    private String trace;
    private List<String> tags = new ArrayList<>();

    protected Map<String, Object> parameters = new HashMap<>();

    public LogEventDTO(String deploymentIdentifier, String containerIdentifier) {
        parameters.put(DEPLOYMENT_IDENTIFIER, deploymentIdentifier);
        parameters.put(CONTAINER_IDENTIFIER, containerIdentifier);
    }

//    protected void setMessage(String msg, Object... params) {
//        int i = 0;
//        this.message = msg;
//        while (message.contains("{}") && i < params.length) {
//            message = message.replaceFirst("\\{}", params[i++].toString());
//        }
//    }

    public void addTaskParameters() {
        parameters.put(LoggingConst.PARAMETER_TRANSACTION,
                Arrays.asList("transactionID"));
        parameters.put(LoggingConst.PARAMETER_CONTEXT_ID,
                Arrays.asList("contextId"));
        parameters.put(LoggingConst.PARAMETER_LIST,
                Arrays.asList("listID"));
        parameters.put(LoggingConst.PARAMETER_SESSION,
                "SessionId");
        parameters.put(LoggingConst.PARAMETER_ORDER_TYPE,
                "Name");
        parameters.put(LoggingConst.PARAMETER_CONFIG_ID, "ConfigId");
        parameters.put(LoggingConst.PARAMETER_TASK_TYPE, "OrderType");
        parameters.put(LoggingConst.PARAMETER_STATUS, "Status");
    }
}
