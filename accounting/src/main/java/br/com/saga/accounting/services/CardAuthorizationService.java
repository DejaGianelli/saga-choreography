package br.com.saga.accounting.services;

import br.com.saga.accounting.entities.CreditCardAuthorization;
import br.com.saga.messaging.CreditCardAuthorizationFailedEvent;
import br.com.saga.messaging.CreditCardAuthorizedEvent;
import br.com.saga.messaging.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static br.com.saga.messaging.EventType.CREDIT_CARD_AUTHORIZATION_FAILED;
import static br.com.saga.messaging.EventType.CREDIT_CARD_AUTHORIZED;
import static br.com.saga.messaging.KafkaHeader.EVENT_TYPE;
import static java.util.Objects.requireNonNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@Slf4j
@Transactional
public class CardAuthorizationService {

    private final RestClient restClient;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public CardAuthorizationService(RestClient restClient,
                                    KafkaTemplate<String, Object> kafkaTemplate) {
        this.restClient = restClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void tryAuthorize(@NonNull CreditCardAuthorization authorization) {
        try {
            StopWatch watch = new StopWatch();
            watch.start();

            log.info("Making request to card authorization service for customer {}",
                    authorization.getDocument());

            URI uri = UriComponentsBuilder.newInstance()
                    .scheme("http")
                    .host("localhost")
                    .port(8000)
                    .path("cards/{document}/authorization")
                    .buildAndExpand(authorization.getDocument())
                    .toUri();

            CreditCardAuthorizationResponse response = restClient.post()
                    .uri(uri)
                    .contentType(APPLICATION_JSON)
                    .retrieve()
                    .body(CreditCardAuthorizationResponse.class);

            assert response != null;
            requireNonNull(response, "CreditCardAuthorizationResponse is null");

            watch.stop();

            log.info("Card authorization finished for customer {} in {} ms",
                    authorization.getDocument(), watch.getTime());

            Boolean authorized = response.getAuthorized();

            if (authorized) {
                CreditCardAuthorizedEvent payload = CreditCardAuthorizedEvent
                        .builder()
                        .consumerGuid(authorization.getCustomerGuid())
                        .orderGuid(authorization.getId())
                        .build();
                var creditCardAuthEvent = new DomainEvent<>(CREDIT_CARD_AUTHORIZED
                        .getKey(), payload);

                ProducerRecord<String, Object> record = new ProducerRecord<>(
                        "order-events", authorization.getId().toString(),
                        creditCardAuthEvent);
                record.headers().add(EVENT_TYPE.getKey(), CREDIT_CARD_AUTHORIZED
                        .keyBytes());

                kafkaTemplate.send(record).join();

            } else {

                CreditCardAuthorizationFailedEvent payload =
                        CreditCardAuthorizationFailedEvent
                                .builder()
                                .consumerGuid(authorization.getCustomerGuid())
                                .orderGuid(authorization.getId())
                                .build();
                var creditCardAuthEvent = new DomainEvent<>(
                        CREDIT_CARD_AUTHORIZATION_FAILED.getKey(), payload);

                ProducerRecord<String, Object> record = new ProducerRecord<>(
                        "order-events", authorization.getId().toString(),
                        creditCardAuthEvent);
                record.headers().add(EVENT_TYPE.getKey(),
                        CREDIT_CARD_AUTHORIZATION_FAILED.keyBytes());

                kafkaTemplate.send(record).join();
            }

        } catch (RestClientException ex) {
            log.warn("Error when attempting to authorize credit card of " +
                    "customer {}", authorization.getDocument());
            throw ex;
        } catch (Exception ex) {
            log.warn("Unexpected error when attempting to authorize credit card" +
                    " of customer {}", authorization.getDocument());
            throw ex;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreditCardAuthorizationResponse {
        private Boolean authorized;
    }
}
