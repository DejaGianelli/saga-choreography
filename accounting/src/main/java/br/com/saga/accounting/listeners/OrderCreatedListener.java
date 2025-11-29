package br.com.saga.accounting.listeners;

import br.com.saga.accounting.services.OrderCreatedEventProcessService;
import br.com.saga.messaging.DomainEvent;
import br.com.saga.messaging.OrderCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderCreatedListener {

    @Autowired
    OrderCreatedEventProcessService orderCreatedEventProcessService;

    @Autowired
    ObjectMapper objectMapper;

    final TypeReference<DomainEvent<OrderCreatedEvent>> valueTypeRef
            = new TypeReference<>() {
    };

    @KafkaListener(
            topics = "order-events",
            groupId = "accounting-order-created",
            filter = "onlyOrderCreatedEventsFilterStrategy"
    )
    public void handle(String message) throws JsonProcessingException {

        DomainEvent<OrderCreatedEvent> event = objectMapper
                .readValue(message, valueTypeRef);

        orderCreatedEventProcessService.process(event);
    }
}