package br.com.saga.consumer.services;

import br.com.saga.messaging.ConsumerVerifiedEvent;
import br.com.saga.messaging.DomainEvent;
import br.com.saga.messaging.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static br.com.saga.messaging.EventType.CONSUMER_VERIFIED;
import static br.com.saga.messaging.KafkaHeader.EVENT_TYPE;
import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@Transactional
@Slf4j
public class OrderCreatedEventProcessService {

    @Autowired
    ConsumerVerificationService consumerVerificationService;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    public void process(DomainEvent<OrderCreatedEvent> event) {

        boolean verified = consumerVerificationService.verify();

        String key = event.getPayload().getOrderGuid().toString();
        String eventType = CONSUMER_VERIFIED.getKey();
        ConsumerVerifiedEvent payload = ConsumerVerifiedEvent.builder()
                .consumerGuid(event.getPayload().getConsumerGuid())
                .orderGuid(event.getPayload().getOrderGuid())
                .verified(verified)
                .build();

        var consumerVerifiedEvent = new DomainEvent<>(eventType, payload);

        ProducerRecord<String, Object> record = new ProducerRecord<>(
                "order-events", key, consumerVerifiedEvent);
        record.headers().add(EVENT_TYPE.getKey(), eventType.getBytes(UTF_8));

        kafkaTemplate.send(record).join();

        log.info("Consumer {} verified: {}", consumerVerifiedEvent.getPayload()
                .getConsumerGuid(), consumerVerifiedEvent.getPayload().isVerified());
    }
}
