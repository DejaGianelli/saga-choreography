package br.com.saga.consumer.listeners;

import br.com.saga.consumer.services.OrderCreatedEventProcessService;
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

import static br.com.saga.messaging.EventType.ORDER_CREATED_VALUE;
import static br.com.saga.messaging.KafkaHeader.EVENT_TYPE_VALUE;

@Component
@Slf4j
public class OrderEventsListener {

    @Autowired
    OrderCreatedEventProcessService orderCreatedEventProcessService;

    @Autowired
    ObjectMapper objectMapper;

    @KafkaListener(
            topics = "order-events",
            groupId = "consumer-order-processing"
    )
    public void handle(@Payload String message,
                       @Header(name = EVENT_TYPE_VALUE)
                       String eventType) throws JsonProcessingException {

        if (eventType.equals(ORDER_CREATED_VALUE)) {
            TypeReference<DomainEvent<OrderCreatedEvent>> valueTypeRef
                    = new TypeReference<>() {
            };
            DomainEvent<OrderCreatedEvent> event = objectMapper
                    .readValue(message, valueTypeRef);
            orderCreatedEventProcessService.process(event);
        } else {
            log.info("Event {} ignored", eventType);
        }
    }
}