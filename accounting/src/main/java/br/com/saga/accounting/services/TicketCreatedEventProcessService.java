package br.com.saga.accounting.services;

import br.com.saga.accounting.entities.CreditCardAuthorization;
import br.com.saga.accounting.repositories.CreditCardAuthorizationRepository;
import br.com.saga.messaging.CreditCardAuthorizedEvent;
import br.com.saga.messaging.DomainEvent;
import br.com.saga.messaging.TicketCreatedEvent;
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
public class TicketCreatedEventProcessService {

    @Autowired
    CreditCardAuthorizationRepository creditCardAuthorizationRepository;

    @Autowired
    CardAuthorizationService cardAuthorizationService;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    public void process(DomainEvent<TicketCreatedEvent> event) {

        creditCardAuthorizationRepository
                .upsertTicketCreated(event.getPayload().getOrderGuid(),
                        event.getPayload().getConsumerDocument());

        log.info("Ticket {} created for order: {}", event.getPayload()
                .getTicketGuid(), event.getPayload().getOrderGuid());

        CreditCardAuthorization authorization =
                creditCardAuthorizationRepository
                        .findById(event.getPayload().getOrderGuid())
                        .orElseThrow(RuntimeException::new);

        if (!authorization.canBeAuthorized()) {
            return;
        }

        boolean authorized = cardAuthorizationService.tryAuthorize(
                authorization.getDocument());

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
