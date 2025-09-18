package com.producer.shared.scripts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.producer.model.SensorReadingMongo;
import com.producer.repository.SensorReadingMongoRepository;
import com.producer.service.CustomMetricsService;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import io.micrometer.core.instrument.Timer;

@Service
public class MongoScripts {

  @Autowired
  private SensorReadingMongoRepository sensorReadingRepository;

  @Autowired
  private CustomMetricsService metricsService;

  public void generateTemperatureDataMongoDB() {
    try {
      final int BATCH_SIZE = 2000;
      final int TOTAL_READINGS = 50000;
      final double SIN_CONSTANT = 2 * Math.PI / BATCH_SIZE;

      SensorReadingMongo temperatureSensorReading = new SensorReadingMongo();
      SensorReadingMongo humiditySensorReading = new SensorReadingMongo();

      sensorReadingRepository.deleteAll();

      temperatureSensorReading = new SensorReadingMongo();
      temperatureSensorReading.setName("temperatura_lm35");
      temperatureSensorReading.setType("temperature");
      temperatureSensorReading.setReadings(new ArrayList<>());
      sensorReadingRepository.save(temperatureSensorReading);

      humiditySensorReading = new SensorReadingMongo();
      humiditySensorReading.setName("umidade_dht11");
      humiditySensorReading.setType("humidity");
      humiditySensorReading.setReadings(new ArrayList<>());
      sensorReadingRepository.save(humiditySensorReading);

      for (int batch = 0; batch < TOTAL_READINGS; batch += BATCH_SIZE) {
        int currentBatchSize = Math.min(BATCH_SIZE, TOTAL_READINGS - batch);

        List<SensorReadingMongo.Reading> currentTemperatureBatch = new ArrayList<>();
        List<SensorReadingMongo.Reading> currentHumidityBatch = new ArrayList<>();

        for (int i = 0; i < currentBatchSize; i++) {
          int globalIndex = batch + i;

          double temperature = Math.sin(SIN_CONSTANT * globalIndex) * 2 + 15;
          double humidity = Math.sin(SIN_CONSTANT * globalIndex) * 2 + 40;

          SensorReadingMongo.Reading temperatureReading = new SensorReadingMongo.Reading();
          temperatureReading.setValue(temperature);
          temperatureReading.setTimestamp(new Date());

          SensorReadingMongo.Reading humidityReading = new SensorReadingMongo.Reading();
          humidityReading.setValue(humidity);
          humidityReading.setTimestamp(new Date());

          currentTemperatureBatch.add(temperatureReading);
          currentHumidityBatch.add(humidityReading);
        }

        Timer.Sample insertTimer = metricsService.startMongoInsertTimer();
        try {
          sensorReadingRepository.pushReadings(temperatureSensorReading.getId(), currentTemperatureBatch);
          sensorReadingRepository.pushReadings(humiditySensorReading.getId(), currentHumidityBatch);

        } finally {
          metricsService.stopMongoInsertTimer(insertTimer);
        }
      }
      System.out.println("Dados gerados no MongoDB");

    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Falha ao gerar dados de teste no MongoDB", e);
    }
  }

  public void getReadingsFromMONGODatabase() {
    try {
      Timer.Sample queryTimer = metricsService.startMongoReadTimer();
      try {
        SensorReadingMongo temperatureSensorReading = sensorReadingRepository.findByName("temperatura_lm35");
        
      } finally {
        metricsService.stopMongoReadTimer(queryTimer);
      }

      Timer.Sample queryTimer2 = metricsService.startMongoReadTimer();
      try {
        SensorReadingMongo humiditySensorReading = sensorReadingRepository.findByName("umidade_dht11");
        
      } finally {
        metricsService.stopMongoReadTimer(queryTimer2);
      }

      System.out.println("Dados recuperados do MongoDB");

    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Falha ao buscar dados do MongoDB", e);
    }
  }
}
