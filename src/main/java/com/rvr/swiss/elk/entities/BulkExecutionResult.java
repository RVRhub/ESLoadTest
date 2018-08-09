package com.rvr.swiss.elk.entities;

import lombok.Data;

@Data
public class BulkExecutionResult {
    private Integer count;
    private long failedCalls;
    private long totalExecutionTime;
    private long startTime;
    private long finishTime;

    public BulkExecutionResult(Integer count, long failedCalls, long totalExecutionTime, long startTime, long finishTime) {
        this.count = count;
        this.failedCalls = failedCalls;
        this.totalExecutionTime = totalExecutionTime;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }
}
