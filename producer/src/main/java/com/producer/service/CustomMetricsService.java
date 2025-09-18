package com.producer.service;

import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class CustomMetricsService {

  private final Counter kafkaSendCounter;
  private final Timer postgresInsertTimer;
  private final Timer postgresReadTimer;
  private final Timer mongoInsertTimer;
  private final Timer mongoReadTimer;

  public CustomMetricsService(MeterRegistry meterRegistry) {
    kafkaSendCounter = Counter.builder("kafka.send.total")
        .description("Total number of Kafka send operations")
        .register(meterRegistry);


    postgresInsertTimer = Timer.builder("postgres.insert.duration")
        .description("Duration of PostgreSQL insert operations")
        .register(meterRegistry);

    postgresReadTimer = Timer.builder("postgres.read.duration")
        .description("Duration of PostgreSQL read operations")
        .register(meterRegistry);


    mongoInsertTimer = Timer.builder("mongo.insert.duration")
        .publishPercentileHistogram()
        .publishPercentiles(0.5, 0.95, 0.99)
        .minimumExpectedValue(Duration.ofMillis(10))
        .maximumExpectedValue(Duration.ofSeconds(1))
        .description("Duration of MongoDB insert operations")
        .register(meterRegistry);

    mongoReadTimer = Timer.builder("mongo.read.duration")
        .publishPercentileHistogram()
        .publishPercentiles(0.5, 0.95, 0.99)
        .minimumExpectedValue(Duration.ofMillis(10))
        .maximumExpectedValue(Duration.ofSeconds(5))
        .description("Duration of MongoDB read operations")
        .register(meterRegistry);
  }

  public Timer.Sample startPostgresInsertTimer() {
    return Timer.start();
  }

  public void stopPostgresInsertTimer(Timer.Sample sample) {
    sample.stop(postgresInsertTimer);
  }

  public void incrementKafkaSend() {
    kafkaSendCounter.increment();
  }

  public Timer.Sample startPostgresReadTimer() {
    return Timer.start();
  }

  public void stopPostgresReadTimer(Timer.Sample sample) {
    sample.stop(postgresReadTimer);
  }

  public Timer.Sample startMongoInsertTimer() {
    return Timer.start();
  }

  public void stopMongoInsertTimer(Timer.Sample sample) {
    sample.stop(mongoInsertTimer);
  }

  public Timer.Sample startMongoReadTimer() {
    return Timer.start();
  }

  public void stopMongoReadTimer(Timer.Sample sample) {
    sample.stop(mongoReadTimer);
  }
}