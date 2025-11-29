package br.com.saga.messaging;

import lombok.Getter;

import static java.nio.charset.StandardCharsets.UTF_8;

@Getter
public enum EventType {

    CREDIT_CARD_AUTHORIZED("CreditCardAuthorized"),
    TICKET_CREATED("TicketCreated"),
    CONSUMER_VERIFIED("ConsumerVerified"),
    ORDER_CREATED("OrderCreated"),
    ORDER_APPROVED("OrderApproved");

    private final String key;

    EventType(String key) {
        this.key = key;
    }

    public byte[] keyBytes() {
        return this.getKey().getBytes(UTF_8);
    }
}
