package com.rvr.swiss.elk.entities;

import com.rvr.swiss.elk.dto.LogEventDTO;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class ExecutionResult {
    private LogEventDTO logEventDTO;
    private ExecutionStatus status;
    private long executionTime;
    private long currentTime;
    private long countExecutedMessage;

    public ExecutionResult(LogEventDTO logEventDTO, ExecutionStatus status, long executionTime) {
        this.logEventDTO = logEventDTO;
        this.status = status;
        this.executionTime = executionTime;
    }

    public LogEventDTO getLogEventDTO() {
        return logEventDTO;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public long getCountExecutedMessage() {
        return countExecutedMessage;
    }

    public void setCountExecutedMessage(long countExecutedMessage) {
        this.countExecutedMessage = countExecutedMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExecutionResult that = (ExecutionResult) o;
        return executionTime == that.executionTime &&
                currentTime == that.currentTime &&
                countExecutedMessage == that.countExecutedMessage &&
                Objects.equals(logEventDTO, that.logEventDTO) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {

        return Objects.hash(logEventDTO, status, executionTime, currentTime, countExecutedMessage);
    }

    @Override
    public String toString() {
        return "ExecutionResult{" +
              //  "logEventDTO=" + logEventDTO +
                ", status='" + status + '\'' +
                ", executionTime=" + executionTime/1000000 +
                ", currentTime=" + currentTime +
                ", currentTime2=" + new SimpleDateFormat("HH:mm:ss:SSS").format(currentTime) +
                ", countExecutedMessage=" + countExecutedMessage +
                '}';
    }
}
