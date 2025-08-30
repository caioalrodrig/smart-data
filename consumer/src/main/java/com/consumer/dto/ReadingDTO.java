package com.consumer.dto;

import java.sql.Timestamp;

public record ReadingDTO(
    Integer value,
    Timestamp timestamp){
}