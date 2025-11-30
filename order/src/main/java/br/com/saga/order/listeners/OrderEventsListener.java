package br.com.saga.order.listeners;


import br.com.saga.messaging.CreditCardAuthorizationFailedEvent;
import br.com.saga.messaging.CreditCardAuthorizedEvent;
import br.com.saga.messaging.DomainEvent;
import br.com.saga.order.services.CreditCardAuthorizationFailedEventProcessService;
import br.com.saga.order.services.CreditCardAuthorizedEventProcessService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static br.com.saga.messaging.EventType.CREDIT_CARD_AUTHORIZATION_FAILED_VALUE;
import static br.com.saga.messaging.EventType.CREDIT_CARD_AUTHORIZED_VALUE;
import static br.com.saga.messaging.KafkaHeader.EVENT_TYPE_VALUE;

@Component
@Slf4j
public class OrderEventsListener {


    private final CreditCardAuthorizedEventProcessService
            creditCardAuthorizedEventProcessService;

    private final CreditCardAuthorizationFailedEventProcessService
            creditCardAuthorizationFailedEventProcessService;

    private final ObjectMapper objectMapper;

    @Autowired
    public OrderEventsListener(CreditCardAuthorizedEventProcessService
                                       creditCardAuthorizedEventProcessService,
                               CreditCardAuthorizationFailedEventProcessService
                                       creditCardAuthorizationFailedEventProcessService,
                               ObjectMapper objectMapper) {
        this.creditCardAuthorizedEventProcessService =
                creditCardAuthorizedEventProcessService;
        this.creditCardAuthorizationFailedEventProcessService =
                creditCardAuthorizationFailedEventProcessService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "order-events",
            groupId = "order-order-processing"
    )
    public void handle(@Payload String message,
                       @Header(name = EVENT_TYPE_VALUE)
                       String eventType) throws JsonProcessingException {

        switch (eventType) {

            case CREDIT_CARD_AUTHORIZED_VALUE: {
                TypeReference<DomainEvent<CreditCardAuthorizedEvent>> valueTypeRef
                        = new TypeReference<>() {
                };
                DomainEvent<CreditCardAuthorizedEvent> event = objectMapper
                        .readValue(message, valueTypeRef);
                creditCardAuthorizedEventProcessService.process(event);
            }
            break;

            case CREDIT_CARD_AUTHORIZATION_FAILED_VALUE: {
                TypeReference<DomainEvent<CreditCardAuthorizationFailedEvent>> valueTypeRef
                        = new TypeReference<>() {
                };
                DomainEvent<CreditCardAuthorizationFailedEvent> event = objectMapper
                        .readValue(message, valueTypeRef);
                creditCardAuthorizationFailedEventProcessService.process(event);
            }
            break;

            default:
                log.info("Event {} ignored", eventType);
        }
    }
}