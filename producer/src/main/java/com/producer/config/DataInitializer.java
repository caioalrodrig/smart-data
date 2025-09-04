package com.producer.config;

import com.producer.model.Farm;
import com.producer.model.Sensor;
import com.producer.model.Reading;
import com.producer.repository.FarmRepository;
import com.producer.repository.SensorRepository;
import com.producer.repository.ReadingRepository;
import com.producer.service.ProducerReadingService;
import com.producer.shared.scripts.MongoScripts;
import com.producer.service.CustomMetricsService;
import com.producer.shared.scripts.PostgresScripts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.Timer;

import java.util.List;
import java.util.ArrayList;

import java.sql.Timestamp;
import java.time.Instant;

@Component
public class DataInitializer implements CommandLineRunner {

  @Autowired
  private FarmRepository farmRepository;

  @Autowired
  private SensorRepository sensorRepository;

  @Autowired
  private ReadingRepository readingRepository;

  @Autowired
  private ProducerReadingService producerReadingService;

  @Autowired
  private CustomMetricsService metricsService;

  @Autowired
  private MongoScripts mongoScripts;

  @Autowired
  private PostgresScripts postgresScripts;


  @Override
  public void run(String... args) throws Exception {
    try {
      /*
       * postgresScripts.generateTemperatureData();
       * postgresScripts.getReadingsFromDatabase();
       * postgresScripts.sendReadingsToTopics();
       */

      mongoScripts.generateTemperatureDataMongoDB();
      mongoScripts.getReadingsFromMONGODatabase();
      
      System.out.println("Inicialização concluída");
    } catch (Exception e) {
      System.err.println("Erro durante inicialização: " + e.getMessage());
      e.printStackTrace();
      throw e;
    }
  }
}
