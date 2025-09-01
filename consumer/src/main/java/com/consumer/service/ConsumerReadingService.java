package com.consumer.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerReadingService {
  @KafkaListener(topics = "temperature", groupId = "temperature-group")
  public void consume(String temperatureValue) {
    System.out.println("🌡️ TEMPERATURA RECEBIDA: " + temperatureValue + "°C");
  }

  @KafkaListener(topics = "humidity", groupId = "humidity-group")
  public void consumeHumidity(String humidityValue) {
    System.out.println("🌡️ UMIDADE RECEBIDA: " + humidityValue + "%");
  }
}
