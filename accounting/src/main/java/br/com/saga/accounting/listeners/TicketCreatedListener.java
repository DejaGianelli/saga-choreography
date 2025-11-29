package br.com.saga.accounting.listeners;

import br.com.saga.accounting.services.TicketCreatedEventProcessService;
import br.com.saga.messaging.DomainEvent;
import br.com.saga.messaging.TicketCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TicketCreatedListener {

    @Autowired
    TicketCreatedEventProcessService ticketCreatedEventProcessService;

    @Autowired
    ObjectMapper objectMapper;

    private final TypeReference<DomainEvent<TicketCreatedEvent>> valueTypeRef
            = new TypeReference<>() {
    };

    @KafkaListener(
            groupId = "accounting-ticket-created",
            topics = "order-events",
            filter = "onlyTicketCreatedEventsFilterStrategy"
    )
    public void handle(String message) throws JsonProcessingException {

        DomainEvent<TicketCreatedEvent> event = objectMapper
                .readValue(message, valueTypeRef);

        ticketCreatedEventProcessService.process(event);
    }
}