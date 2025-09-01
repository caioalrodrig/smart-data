package com.producer.dto;

import java.sql.Timestamp;

public record ReadingDTO(
    Double value,
    Timestamp timestamp){
}