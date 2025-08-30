package com.producer.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.producer.dto.ReadingDTO;
import com.producer.repository.ReadingRepository;
import com.producer.dto.mapper.ReadingMapper;

@Service
public class ReadingService {

  private final ReadingRepository readingRepository;

  public ReadingService(ReadingRepository readingRepository) {
    this.readingRepository = readingRepository;
  }

  public List<ReadingDTO> getReadings() {
    return readingRepository
      .findAll()
      .stream()
      .map(ReadingMapper::toDTO)
      .collect(Collectors.toList());
  }

  public ReadingDTO findReadingById(Long id) {
    return readingRepository
      .findById(id)
      .map(ReadingMapper::toDTO)
      .orElse(null);
  }
}
