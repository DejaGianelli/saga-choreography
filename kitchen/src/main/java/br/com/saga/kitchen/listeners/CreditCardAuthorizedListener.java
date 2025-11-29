package br.com.saga.kitchen.listeners;

import br.com.saga.kitchen.services.CreditCardAuthorizedEventProcessService;
import br.com.saga.messaging.CreditCardAuthorizedEvent;
import br.com.saga.messaging.DomainEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CreditCardAuthorizedListener {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CreditCardAuthorizedEventProcessService creditCardAuthorizedEventProcessService;

    final TypeReference<DomainEvent<CreditCardAuthorizedEvent>> valueTypeRef
            = new TypeReference<>() {
    };

    @KafkaListener(
            groupId = "kitchen-credit-card-authorized",
            topics = "order-events",
            filter = "onlyCreditCardAuthorizedFilterStrategy"
    )
    public void handle(String message) throws JsonProcessingException {

        DomainEvent<CreditCardAuthorizedEvent> event = objectMapper
                .readValue(message, valueTypeRef);

        creditCardAuthorizedEventProcessService.process(event);
    }
}