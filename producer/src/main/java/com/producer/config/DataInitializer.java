package com.producer.config;

import com.producer.model.Farm;
import com.producer.model.Sensor;
import com.producer.model.Reading;
import com.producer.repository.FarmRepository;
import com.producer.repository.SensorRepository;
import com.producer.repository.ReadingRepository;
import com.producer.service.ProducerReadingService;
import com.producer.service.CustomMetricsService;
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

  private List<Reading> temperatureReadings = new ArrayList<>();
  private List<Reading> humidityReadings = new ArrayList<>();

  @Override
  public void run(String... args) throws Exception {
    try {
      Farm farm1 = createFarm();
      createTemperatureSensor(farm1);
      createHumiditySensor(farm1);

      generateTemperatureData();
      getReadingsFromDatabase();
      sendReadingsToTopics();

      System.out.println("Inicialização concluída");
    } catch (Exception e) {
      System.err.println("Erro durante inicialização: " + e.getMessage());
      e.printStackTrace();
      throw e;
    }
  }

  private Farm createFarm() {
    try {
      Farm farm1 = new Farm();
      farm1.setName("Fazenda Santa Maria");
      Farm savedFarm = farmRepository.save(farm1);
      System.out.println("Fazenda criada");
      return savedFarm;
    } catch (Exception e) {
      System.err.println("Erro ao criar fazenda: " + e.getMessage());
      throw new RuntimeException("Falha ao criar fazenda", e);
    }
  }

  private Sensor createTemperatureSensor(Farm farm) {
    try {
      Sensor sensor = new Sensor();
      sensor.setStatus("Ativo");
      sensor.setName("temperatura_lm35");
      sensor.setFarm(farm);
      Sensor savedSensor = sensorRepository.save(sensor);
      System.out.println("Sensor temperatura criado");
      return savedSensor;
    } catch (Exception e) {
      System.err.println("Erro ao criar sensor de temperatura: " + e.getMessage());
      throw new RuntimeException("Falha ao criar sensor de temperatura", e);
    }
  }

  private Sensor createHumiditySensor(Farm farm) {
    try {
      Sensor sensor = new Sensor();
      sensor.setStatus("Ativo");
      sensor.setName("umidade_dht11");
      sensor.setFarm(farm);
      Sensor savedSensor = sensorRepository.save(sensor);
      return savedSensor;
    } catch (Exception e) {
      throw new RuntimeException("Falha ao criar sensor de umidade", e);
    }
  }

  private void generateTemperatureData() {
    try {
      temperatureReadings.clear();
      humidityReadings.clear();

      Sensor temperatureSensor = sensorRepository.findByName("temperatura_lm35");
      Sensor humiditySensor = sensorRepository.findByName("umidade_dht11");

      if (temperatureSensor == null || humiditySensor == null) {
        throw new RuntimeException("Sensores não encontrados no banco de dados");
      }

      final int BATCH_SIZE = 2000;
      final int TOTAL_READINGS = 50000;
      final double SIN_CONSTANT = 2 * Math.PI / BATCH_SIZE;

      for (int batch = 0; batch < TOTAL_READINGS; batch += BATCH_SIZE) {
        int currentBatchSize = Math.min(BATCH_SIZE, TOTAL_READINGS - batch);

        List<Reading> currentTemperatureBatch = new ArrayList<>();
        List<Reading> currentHumidityBatch = new ArrayList<>();

        for (int i = 0; i < currentBatchSize; i++) {
          int globalIndex = batch + i;

          double temperature = Math.sin(SIN_CONSTANT * globalIndex) * 2 + 15;
          double humidity = Math.sin(SIN_CONSTANT * globalIndex) * 2 + 40;

          Reading temperatureReading = new Reading();
          temperatureReading.setSensor(temperatureSensor);
          temperatureReading.setValue(temperature);
          temperatureReading.setTimestamp(Timestamp.from(Instant.now()));

          Reading humidityReading = new Reading();
          humidityReading.setSensor(humiditySensor);
          humidityReading.setValue(humidity);
          humidityReading.setTimestamp(Timestamp.from(Instant.now()));

          currentTemperatureBatch.add(temperatureReading);
          currentHumidityBatch.add(humidityReading);

        }

        Timer.Sample insertTimer = metricsService.startPostgresInsertTimer();
        try {
          readingRepository.saveAll(currentTemperatureBatch);
          readingRepository.saveAll(currentHumidityBatch);

          temperatureReadings.addAll(currentTemperatureBatch);
          humidityReadings.addAll(currentHumidityBatch);

          metricsService.incrementPostgresInsert();
          metricsService.incrementPostgresInsert();

        } finally {
          metricsService.stopPostgresInsertTimer(insertTimer);
        }
      }
      System.out.println("Dados gerados");

    } catch (Exception e) {
      throw new RuntimeException("Falha ao gerar dados de teste", e);
    }
  }

  private void getReadingsFromDatabase() {
    try {
      Sensor temperatureSensor = sensorRepository.findByName("temperatura_lm35");
      Sensor humiditySensor = sensorRepository.findByName("umidade_dht11");

      if (temperatureSensor == null || humiditySensor == null) {
        throw new RuntimeException("Sensores não encontrados no banco de dados");
      }

      Timer.Sample queryTimer = metricsService.startPostgresReadTimer();
      try {
        temperatureReadings = readingRepository.findAllBySensorId(temperatureSensor.getId());
        metricsService.incrementPostgresRead();
      } finally {
        metricsService.stopPostgresReadTimer(queryTimer);
      }

      Timer.Sample queryTimer2 = metricsService.startPostgresReadTimer();
      try {
        humidityReadings = readingRepository.findAllBySensorId(humiditySensor.getId());
        metricsService.incrementPostgresRead();
      } finally {
        metricsService.stopPostgresReadTimer(queryTimer2);
      }

      System.out.println("Dados recuperados");

    } catch (Exception e) {
      throw new RuntimeException("Falha ao buscar dados do banco", e);
    }
  }

  private void sendReadingsToTopics() {
    try {
      if (temperatureReadings.isEmpty() && humidityReadings.isEmpty()) {
        return;
      }

      for (Reading reading : temperatureReadings) {
        try {
          metricsService.incrementKafkaSend();
          producerReadingService.sendTemperatureReading(reading);
        } catch (Exception e) {
          System.err.println("Erro ao enviar leitura de temperatura: " + e.getMessage());
        }
      }

      for (Reading reading : humidityReadings) {
        try {
          metricsService.incrementKafkaSend();
          producerReadingService.sendHumidityReading(reading);
        } catch (Exception e) {
          System.err.println("Erro ao enviar leitura de umidade: " + e.getMessage());
        }
      }

      System.out.println("Dados enviados");

    } catch (Exception e) {
      System.err.println("Erro ao enviar dados para tópicos: " + e.getMessage());
      throw new RuntimeException("Falha ao enviar dados para tópicos", e);
    }
  }
}
