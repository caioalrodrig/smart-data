package com.producer.config;

import com.producer.model.Farm;
import com.producer.model.Sensor;
import com.producer.model.Reading;
import com.producer.repository.FarmRepository;
import com.producer.repository.SensorRepository;
import com.producer.repository.ReadingRepository;
import com.producer.service.ProducerReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Random;

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

    private Random random = new Random();

    private List<Reading> temperatureReadings = new ArrayList<>();
    private List<Reading> humidityReadings = new ArrayList<>();

    @Override
    public void run(String... args) throws Exception {
        // Criar duas fazendas
        Farm farm1 = new Farm();
        farm1.setName("Fazenda Santa Maria");
        farmRepository.save(farm1);

        Farm farm2 = new Farm();
        farm2.setName("Fazenda Boa Vista");
        farmRepository.save(farm2);

        // Criar dois sensores
        Sensor sensor1 = new Sensor();
        sensor1.setFarm(farm1);
        sensor1.setStatus("Ativo");
        sensor1.setName("temperatura_lm35");
        sensorRepository.save(sensor1);

        Sensor sensor2 = new Sensor();
        sensor2.setFarm(farm2);
        sensor2.setStatus("Ativo");
        sensor2.setName("umidade_dht11");
        sensorRepository.save(sensor2);

        System.out.println("=== Dados de teste inicializados com sucesso! ===");
        System.out.println("Fazendas criadas: " + farmRepository.count());
        System.out.println("Sensores criados: " + sensorRepository.count());
        System.out.println("================================================");

        generateTemperatureData();

        sendReadingsToTopics();
    }

    private void generateTemperatureData() {
        try {
            temperatureReadings.clear();
            humidityReadings.clear();

            Sensor temperatureSensor = sensorRepository.findByName("temperatura_lm35");
            Sensor humiditySensor = sensorRepository.findByName("umidade_dht11");

            // populate reading database
            for (int i = 0; i < 5000; i++) {
                if (temperatureSensor == null || humiditySensor == null) {
                    break;
                }

                double temperature = Math.sin(2 * Math.PI * i / 10000) * 2 + 15;
                double humidity = Math.sin(2 * Math.PI * i / 10000) * 2 + 40;

                Reading temperatureReading = new Reading();
                temperatureReading.setSensor(temperatureSensor);
                temperatureReading.setValue(temperature);
                temperatureReading.setTimestamp(Timestamp.from(Instant.now()));

                Reading humidityReading = new Reading();
                humidityReading.setSensor(humiditySensor);
                humidityReading.setValue(humidity);
                humidityReading.setTimestamp(Timestamp.from(Instant.now()));

                temperatureReadings.add(temperatureReading);
                humidityReadings.add(humidityReading);
            }

            readingRepository.saveAll(temperatureReadings);
            readingRepository.saveAll(humidityReadings);
        } catch (Exception e) {
            System.err.println("Erro ao gerar dados de temperatura: " + e.getMessage());
        }
    }

    private void sendReadingsToTopics() {
        for (Reading reading : temperatureReadings) {
            producerReadingService.sendTemperatureReading(reading);
        }
        for (Reading reading : humidityReadings) {
            producerReadingService.sendHumidityReading(reading);
        }
    }
}
