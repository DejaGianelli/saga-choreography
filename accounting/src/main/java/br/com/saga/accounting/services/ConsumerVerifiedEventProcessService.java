package br.com.saga.accounting.services;

import br.com.saga.accounting.entities.CreditCardAuthorization;
import br.com.saga.accounting.repositories.CreditCardAuthorizationRepository;
import br.com.saga.messaging.ConsumerVerifiedEvent;
import br.com.saga.messaging.CreditCardAuthorizedEvent;
import br.com.saga.messaging.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static br.com.saga.messaging.EventType.CREDIT_CARD_AUTHORIZED;
import static br.com.saga.messaging.KafkaHeader.EVENT_TYPE;

@Service
@Transactional
@Slf4j
public class ConsumerVerifiedEventProcessService {

    @Autowired
    CreditCardAuthorizationRepository creditCardAuthorizationRepository;

    @Autowired
    CardAuthorizationService cardAuthorizationService;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    public void process(DomainEvent<ConsumerVerifiedEvent> event) {

        log.info("Consumer {} verified: {}", event.getPayload().getConsumerGuid(),
                event.getPayload().isVerified());

        CreditCardAuthorization authorization =
                creditCardAuthorizationRepository
                        .findById(event.getPayload().getOrderGuid())
                        .orElseThrow(RuntimeException::new);

        authorization.setCustomerVerified(event.getPayload().isVerified());

        if (!authorization.canBeAuthorized()) {
            return;
        }

        boolean authorized = cardAuthorizationService.tryAuthorize(event
                .getPayload().getConsumerDocument());

        if (authorized) {
            CreditCardAuthorizedEvent payload = CreditCardAuthorizedEvent
                    .builder()
                    .consumerGuid(authorization.getCustomerGuid())
                    .orderGuid(authorization.getId())
                    .build();
            var creditCardAuthEvent = new DomainEvent<>(CREDIT_CARD_AUTHORIZED
                    .getKey(), payload);

            ProducerRecord<String, Object> record = new ProducerRecord<>(
                    "order-events", event.getPayload().getOrderGuid().toString(),
                    creditCardAuthEvent);
            record.headers().add(EVENT_TYPE.getKey(), CREDIT_CARD_AUTHORIZED
                    .keyBytes());
            kafkaTemplate.send(record).join();
        }
    }
}
