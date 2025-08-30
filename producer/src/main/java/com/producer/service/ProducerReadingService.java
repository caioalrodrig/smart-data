package com.producer.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.producer.dto.ReadingDTO;
import com.producer.model.Reading;
import com.producer.dto.mapper.ReadingMapper;

@Service
public class ProducerReadingService {
  private final KafkaTemplate<String, ReadingDTO> kafkaTemplate;

  public ProducerReadingService(KafkaTemplate<String, ReadingDTO> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendTemperatureReading(Reading reading) {
    ReadingDTO readingDTO = ReadingMapper.toDTO(reading);
    kafkaTemplate.send("temperature", readingDTO);
    System.out.println("Temperatura enviada para Kafka: " + readingDTO.value() + "°C");
  }
}
