package br.com.saga.order.listeners;

import br.com.saga.messaging.CreditCardAuthorizedEvent;
import br.com.saga.messaging.DomainEvent;
import br.com.saga.messaging.OrderApprovedEvent;
import br.com.saga.order.entities.Order;
import br.com.saga.order.repositories.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static br.com.saga.messaging.EventType.ORDER_APPROVED;
import static br.com.saga.messaging.KafkaHeader.EVENT_TYPE;
import static br.com.saga.order.entities.Order.Status.APPROVED;

@Component
@Slf4j
@Transactional
public class CreditCardAuthorizedListener {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    OrderRepository orderRepository;

    final TypeReference<DomainEvent<CreditCardAuthorizedEvent>> valueTypeRef
            = new TypeReference<>() {
    };

    @KafkaListener(
            groupId = "order-credit-card-authorized",
            topics = "order-events",
            filter = "onlyCreditCardAuthorizedFilterStrategy"
    )
    public void handle(String message) throws JsonProcessingException {

        DomainEvent<CreditCardAuthorizedEvent> eventRead = objectMapper
                .readValue(message, valueTypeRef);

        CreditCardAuthorizedEvent readPayload = eventRead.getPayload();

        Order order = orderRepository
                .findById(readPayload.getOrderGuid())
                .orElseThrow(RuntimeException::new);

        order.setStatus(APPROVED);

        String key = order.getGuid().toString();
        OrderApprovedEvent payload = OrderApprovedEvent.builder()
                .consumerGuid(order.getConsumerGuid())
                .orderGuid(order.getGuid())
                .build();
        var event = new DomainEvent<>(ORDER_APPROVED.getKey(), payload);

        ProducerRecord<String, Object> record = new ProducerRecord<>(
                "order-events", key, event);
        record.headers().add(EVENT_TYPE.getKey(), ORDER_APPROVED.keyBytes());

        kafkaTemplate.send(record).join();
    }
}