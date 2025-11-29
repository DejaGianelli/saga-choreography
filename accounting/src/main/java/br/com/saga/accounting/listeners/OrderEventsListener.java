package br.com.saga.accounting.listeners;

import br.com.saga.accounting.services.ConsumerVerifiedEventProcessService;
import br.com.saga.accounting.services.OrderCreatedEventProcessService;
import br.com.saga.accounting.services.TicketCreatedEventProcessService;
import br.com.saga.messaging.ConsumerVerifiedEvent;
import br.com.saga.messaging.DomainEvent;
import br.com.saga.messaging.OrderCreatedEvent;
import br.com.saga.messaging.TicketCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static br.com.saga.messaging.EventType.*;
import static br.com.saga.messaging.KafkaHeader.EVENT_TYPE_VALUE;

@Component
@Slf4j
public class OrderEventsListener {

    @Autowired
    TicketCreatedEventProcessService ticketCreatedEventProcessService;

    @Autowired
    OrderCreatedEventProcessService orderCreatedEventProcessService;

    @Autowired
    ConsumerVerifiedEventProcessService consumerVerifiedEventProcessService;

    @Autowired
    ObjectMapper objectMapper;

    @KafkaListener(
            topics = "order-events",
            groupId = "accounting-order-processing"
    )
    public void handle(@Payload String message,
                       @Header(name = EVENT_TYPE_VALUE)
                       String eventType) throws JsonProcessingException {

        switch (eventType) {
            case CONSUMER_VERIFIED_VALUE: {
                TypeReference<DomainEvent<ConsumerVerifiedEvent>> valueTypeRef
                        = new TypeReference<>() {
                };
                DomainEvent<ConsumerVerifiedEvent> event = objectMapper
                        .readValue(message, valueTypeRef);
                consumerVerifiedEventProcessService.process(event);
            }
            break;

            case ORDER_CREATED_VALUE: {
                TypeReference<DomainEvent<OrderCreatedEvent>> valueTypeRef
                        = new TypeReference<>() {
                };
                DomainEvent<OrderCreatedEvent> event = objectMapper
                        .readValue(message, valueTypeRef);
                orderCreatedEventProcessService.process(event);
            }
            break;

            case TICKET_CREATED_VALUE: {
                TypeReference<DomainEvent<TicketCreatedEvent>> valueTypeRef
                        = new TypeReference<>() {
                };
                DomainEvent<TicketCreatedEvent> event = objectMapper
                        .readValue(message, valueTypeRef);
                ticketCreatedEventProcessService.process(event);
            }
            break;

            default:
                log.info("Event {} ignored", eventType);
        }
    }
}