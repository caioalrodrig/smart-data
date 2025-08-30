package com.producer.dto.mapper;

import com.producer.dto.ReadingDTO;
import com.producer.model.Reading;

public class ReadingMapper {

  public static ReadingDTO toDTO(Reading reading) {
    return new ReadingDTO(reading.getValue(), reading.getTimestamp());
  }
}
