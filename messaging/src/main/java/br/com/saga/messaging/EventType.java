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

    public static final String CREDIT_CARD_AUTHORIZED_VALUE = "CreditCardAuthorized";
    public static final String TICKET_CREATED_VALUE = "TicketCreated";
    public static final String CONSUMER_VERIFIED_VALUE = "ConsumerVerified";
    public static final String ORDER_CREATED_VALUE = "OrderCreated";
    public static final String ORDER_APPROVED_VALUE = "OrderApproved";

    private final String key;

    EventType(String key) {
        this.key = key;
    }

    public byte[] keyBytes() {
        return this.getKey().getBytes(UTF_8);
    }
}
