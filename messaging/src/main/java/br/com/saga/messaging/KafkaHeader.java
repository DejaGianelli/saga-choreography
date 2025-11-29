package br.com.saga.messaging;

import lombok.Getter;

@Getter
public enum KafkaHeader {

    EVENT_TYPE("EventType");

    private final String key;

    KafkaHeader(String key) {
        this.key = key;
    }
}
