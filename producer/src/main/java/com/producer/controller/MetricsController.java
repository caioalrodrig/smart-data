package com.producer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

  @Autowired
  private MeterRegistry meterRegistry;

  @GetMapping("/postgres")
  public Map<String, Object> getMetricsSummary() {
    Map<String, Object> summary = new HashMap<>();

    Counter postgresCounter = meterRegistry.find("postgres.insert.total").counter();
    Counter kafkaCounter = meterRegistry.find("kafka.send.total").counter();

    Timer postgresInsertTimer = meterRegistry.find("postgres.insert.duration").timer();
    Timer postgresReadTimer = meterRegistry.find("postgres.read.duration").timer();

    summary.put("postgres_insert", Map.of(
        "total_inserts", postgresCounter != null ? postgresCounter.count() : 0,
        "avg_insert_time_ms", postgresInsertTimer != null ? postgresInsertTimer.mean(TimeUnit.MILLISECONDS) : 0,
        "max_insert_time_ms", postgresInsertTimer != null ? postgresInsertTimer.max(TimeUnit.MILLISECONDS) : 0));

    summary.put("kafka", Map.of(
        "total_sends", kafkaCounter != null ? kafkaCounter.count() : 0));

    summary.put("postgres_read", Map.of(
        "total_executions", postgresReadTimer != null ? postgresReadTimer.count() : 0,
        "avg_execution_time_ms", postgresReadTimer != null ? postgresReadTimer.mean(TimeUnit.MILLISECONDS) : 0,
        "max_execution_time_ms", postgresReadTimer != null ? postgresReadTimer.max(TimeUnit.MILLISECONDS) : 0));

    return summary;
  }

  @GetMapping("/mongo")
  public Map<String, Object> getMetricsSummaryMongo() {
    Map<String, Object> summary = new HashMap<>();

    Counter mongoCounter = meterRegistry.find("mongo.insert.total").counter();
    Timer mongoInsertTimer = meterRegistry.find("mongo.insert.duration").timer();
    Timer mongoReadTimer = meterRegistry.find("mongo.read.duration").timer();
    
    summary.put("mongo_insert", Map.of(
        "total_inserts", mongoCounter != null ? mongoCounter.count() : 0,
        "avg_insert_time_ms", mongoInsertTimer != null ? mongoInsertTimer.mean(TimeUnit.MILLISECONDS) : 0,
        "max_insert_time_ms", mongoInsertTimer != null ? mongoInsertTimer.max(TimeUnit.MILLISECONDS) : 0));

    summary.put("mongo_read", Map.of(
        "total_executions", mongoReadTimer != null ? mongoReadTimer.count() : 0,
        "avg_execution_time_ms", mongoReadTimer != null ? mongoReadTimer.mean(TimeUnit.MILLISECONDS) : 0,
        "max_execution_time_ms", mongoReadTimer != null ? mongoReadTimer.max(TimeUnit.MILLISECONDS) : 0));

    return summary;
  }
}
