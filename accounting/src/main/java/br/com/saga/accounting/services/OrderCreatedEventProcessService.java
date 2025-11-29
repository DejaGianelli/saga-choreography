package br.com.saga.accounting.services;

import br.com.saga.accounting.entities.CreditCardAuthorization;
import br.com.saga.accounting.repositories.CreditCardAuthorizationRepository;
import br.com.saga.messaging.DomainEvent;
import br.com.saga.messaging.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class OrderCreatedEventProcessService {

    @Autowired
    CreditCardAuthorizationRepository creditCardAuthorizationRepository;

    public void process(DomainEvent<OrderCreatedEvent> event) {

        CreditCardAuthorization authorization = new CreditCardAuthorization(
                event.getPayload().getOrderGuid(),
                event.getPayload().getConsumerGuid(),
                event.getPayload().getConsumerDocument());

        creditCardAuthorizationRepository.save(authorization);

        log.info("Credit Card Authorization is pending for order {}.",
                event.getPayload().getOrderGuid());
    }
}
