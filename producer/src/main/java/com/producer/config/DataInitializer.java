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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
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
        
        // Iniciar geração contínua de dados de temperatura
        startTemperatureDataGeneration();
    }

    @Scheduled(fixedRate = 3000) // Executa a cada 3 segundos
    public void generateAndSendTemperatureData() {
        try {
            // Gerar temperatura aleatória entre 15-35°C
            int temperature = random.nextInt(21) + 15;
            
            // Buscar o sensor de temperatura
            Sensor tempSensor = sensorRepository.findAll().stream()
                .filter(s -> s.getName().contains("temperatura"))
                .findFirst()
                .orElse(null);
            
            if (tempSensor != null) {
                // Criar nova leitura
                Reading tempReading = new Reading();
                tempReading.setSensor(tempSensor);
                tempReading.setValue(temperature);
                tempReading.setTimestamp(Timestamp.from(Instant.now()));
                
                // Salvar no banco
                readingRepository.save(tempReading);
                
                // Enviar para o Kafka
                producerReadingService.sendTemperatureReading(tempReading);
                
                System.out.println("📊 Nova leitura de temperatura: " + temperature + "°C");
            }
        } catch (Exception e) {
            System.err.println("Erro ao gerar dados de temperatura: " + e.getMessage());
        }
    }

    private void startTemperatureDataGeneration() {
        System.out.println("🚀 Iniciando geração contínua de dados de temperatura...");
        System.out.println("📡 Dados serão enviados para o tópico 'temperature' a cada 3 segundos");
    }
}
