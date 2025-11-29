package br.com.saga.messaging;

import lombok.Getter;

@Getter
public enum KafkaHeader {

    EVENT_TYPE("EventType");

    public static final String EVENT_TYPE_VALUE = "EventType";

    private final String key;

    KafkaHeader(String key) {
        this.key = key;
    }
}
