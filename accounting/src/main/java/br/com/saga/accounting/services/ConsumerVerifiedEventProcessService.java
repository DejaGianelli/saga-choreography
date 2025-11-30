package br.com.saga.accounting.services;

import br.com.saga.accounting.entities.CreditCardAuthorization;
import br.com.saga.accounting.repositories.CreditCardAuthorizationRepository;
import br.com.saga.messaging.ConsumerVerifiedEvent;
import br.com.saga.messaging.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class ConsumerVerifiedEventProcessService {

    @Autowired
    CreditCardAuthorizationRepository creditCardAuthorizationRepository;

    @Autowired
    CardAuthorizationService cardAuthorizationService;

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

        cardAuthorizationService.tryAuthorize(authorization);
    }
}
