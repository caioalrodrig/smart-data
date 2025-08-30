package com.producer.dto;

import java.sql.Timestamp;

public record ReadingDTO(
    Integer value,
    Timestamp timestamp){
}