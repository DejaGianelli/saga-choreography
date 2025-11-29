package br.com.saga.accounting.listeners;

import br.com.saga.accounting.services.ConsumerVerifiedEventProcessService;
import br.com.saga.messaging.ConsumerVerifiedEvent;
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
public class ConsumerVerificationListener {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ConsumerVerifiedEventProcessService consumerVerifiedEventProcessService;

    final TypeReference<DomainEvent<ConsumerVerifiedEvent>> valueTypeRef
            = new TypeReference<>() {
    };

    @KafkaListener(
            groupId = "accounting-consumer-verified",
            topics = "order-events",
            filter = "onlyConsumerVerifiedEventsFilterStrategy"
    )
    public void handle(String message) throws JsonProcessingException {

        DomainEvent<ConsumerVerifiedEvent> event = objectMapper
                .readValue(message, valueTypeRef);

        consumerVerifiedEventProcessService.process(event);
    }
}