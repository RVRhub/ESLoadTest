package com.rvr.swiss.elk.controllers;

import com.rvr.swiss.elk.dto.LogEventDTO;
import com.rvr.swiss.elk.entities.BulkExecutionResult;
import com.rvr.swiss.elk.entities.ExecutionResult;
import com.rvr.swiss.elk.entities.ExecutionStatus;
import com.rvr.swiss.elk.integration.ElasticsearchClient;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/runner")
public class RunELKController {

    private static final Logger LOG = LoggerFactory.getLogger(RunELKController.class);
    public static final int TO_MSEC = 1000000;

    private final ElasticsearchClient elasticsearchClient;

    @Value("${elk.testName}")
    private String testName;

    @Autowired
    public RunELKController(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    private volatile ThreadPoolExecutor threadPoolExecutor;
    private AtomicLong countExecutedMessage = new AtomicLong(0);

    public static final Integer NUMBER_OF_THREAD = 100;


    @GetMapping("/elasticsearch")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendRequestToElasticsearch() {

        LOG.info("Start Test");

        Sender sender = new Sender();
        ExecutionResult result = sender.call();

        LOG.info("Sent: " + result.getLogEventDTO() + ", result: " + result.getExecutionTime());

    }

    @GetMapping("/elk/count/{count}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BulkExecutionResult sendRequestToElasticsearch(@PathVariable Integer count) throws InterruptedException {

        long startTime = System.nanoTime();
        LOG.info("Start Bulk execution");

        List<Callable<ExecutionResult>> senders = preparing(count);
        List<ExecutionResult> result = execute(senders);

        if (LOG.isDebugEnabled()) {
            result.forEach(t -> LOG.debug("Sent: " + t));
        }

        final long[] callStat = new long[2];
        callStat[0] = 0; //maxExecutionTime
        callStat[1] = 0; //failedCalls
        result.forEach(t -> {
            if (t.getExecutionTime() > callStat[0]) {
                callStat[0] = t.getExecutionTime();
            }

            if(ExecutionStatus.ERROR == t.getStatus()){
                callStat[1]++;
            }
        });

        countExecutedMessage.set(0);

        long finishTime = System.nanoTime();
        LOG.info("Total failed calls: " + callStat[1]);
        LOG.info("Total Execution time: " + (finishTime - startTime) / TO_MSEC + " ms.");
        LOG.info("Max Execution time: " + callStat[0] / TO_MSEC + " ms.");
        LOG.info("Bulk execution finished, sent {} requests.", count);

        return new BulkExecutionResult(count, callStat[1], (finishTime - startTime), startTime, finishTime);
    }

    private List<Callable<ExecutionResult>> preparing(@PathVariable Integer count) {
        List<Callable<ExecutionResult>> senders = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            senders.add(new Sender());
        }
        return senders;
    }

    private List<ExecutionResult> execute(List<Callable<ExecutionResult>> senders) throws InterruptedException {
        return getExecutor().invokeAll(senders)
                .parallelStream()
                .map(future -> {
                    try {
                        ExecutionResult executionResult = future.get();
                        executionResult.setCurrentTime(System.nanoTime());
                        executionResult.setCountExecutedMessage(countExecutedMessage.incrementAndGet());

                        return executionResult;
                    } catch (Exception e) {
                        LOG.error("Execution failed", e);
                        throw new IllegalStateException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private ExecutorService getExecutor() {
        if (threadPoolExecutor == null) {
            synchronized (this) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = new ThreadPoolExecutor(NUMBER_OF_THREAD, NUMBER_OF_THREAD,
                            60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
                    threadPoolExecutor.allowCoreThreadTimeOut(true);
                }
            }
        }
        return threadPoolExecutor;
    }


    private class Sender implements Callable<ExecutionResult> {
        private final LogEventDTO logEventDTO;

        public Sender() {
            logEventDTO = getLogEvent();
        }

        @Override
        public ExecutionResult call() {
            ExecutionStatus executionStatus = ExecutionStatus.SUCCESS;
            long startTime = System.nanoTime();

            try {
                elasticsearchClient.send(logEventDTO);
            } catch (Exception e) {
                LOG.error("Execution filed: ");
                executionStatus = ExecutionStatus.ERROR;
            }

            long executionTime = System.nanoTime() - startTime;

            return new ExecutionResult(logEventDTO, executionStatus, executionTime);
        }

        public LogEventDTO getLogEventDTO() {
            return logEventDTO;
        }

        private LogEventDTO getLogEvent() {

            String stackTrace = Arrays.toString(new Exception().getStackTrace());

            LogEventDTO logEvent
                    = new LogEventDTO(testName, testName);
            logEvent.setEventSubTypeID("77777");
            logEvent.setEventTypeID("55555");
            logEvent.setLevel("INFO");
            logEvent.setNode(testName);
            logEvent.setTrace(stackTrace);
            logEvent.setExceptionMessage(stackTrace);
            logEvent.setTags(Arrays.asList("swiss", "elk", "test1"));
            logEvent.addTaskParameters();
            logEvent.setMessageText(stackTrace);
            return logEvent;
        }
    }
}
