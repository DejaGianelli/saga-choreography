package br.com.saga.kitchen.listeners;

import br.com.saga.kitchen.services.CreditCardAuthorizedEventProcessService;
import br.com.saga.kitchen.services.OrderCreatedEventProcessService;
import br.com.saga.messaging.CreditCardAuthorizedEvent;
import br.com.saga.messaging.DomainEvent;
import br.com.saga.messaging.OrderCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static br.com.saga.messaging.EventType.CREDIT_CARD_AUTHORIZED_VALUE;
import static br.com.saga.messaging.EventType.ORDER_CREATED_VALUE;
import static br.com.saga.messaging.KafkaHeader.EVENT_TYPE_VALUE;

@Component
@Slf4j
public class OrderEventsListener {

    @Autowired
    OrderCreatedEventProcessService orderCreatedEventProcessService;

    @Autowired
    CreditCardAuthorizedEventProcessService creditCardAuthorizedEventProcessService;

    @Autowired
    ObjectMapper objectMapper;

    @KafkaListener(
            topics = "order-events",
            groupId = "kitchen-order-processing"
    )
    public void handle(@Payload String message,
                       @Header(name = EVENT_TYPE_VALUE)
                       String eventType) throws JsonProcessingException {

        switch (eventType) {
            case ORDER_CREATED_VALUE: {
                TypeReference<DomainEvent<OrderCreatedEvent>> valueTypeRef
                        = new TypeReference<>() {
                };
                DomainEvent<OrderCreatedEvent> event = objectMapper
                        .readValue(message, valueTypeRef);
                orderCreatedEventProcessService.process(event);
            }
            break;

            case CREDIT_CARD_AUTHORIZED_VALUE: {
                TypeReference<DomainEvent<CreditCardAuthorizedEvent>> valueTypeRef
                        = new TypeReference<>() {
                };
                DomainEvent<CreditCardAuthorizedEvent> event = objectMapper
                        .readValue(message, valueTypeRef);
                creditCardAuthorizedEventProcessService.process(event);
            }
            break;

            default:
                log.info("Event {} ignored", eventType);
        }
    }
}