package br.com.saga.accounting.services;

import br.com.saga.accounting.entities.CreditCardAuthorization;
import br.com.saga.accounting.repositories.CreditCardAuthorizationRepository;
import br.com.saga.messaging.DomainEvent;
import br.com.saga.messaging.TicketCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class TicketCreatedEventProcessService {

    @Autowired
    CreditCardAuthorizationRepository creditCardAuthorizationRepository;

    @Autowired
    CardAuthorizationService cardAuthorizationService;

    public void process(DomainEvent<TicketCreatedEvent> event) {

        log.info("Ticket {} created for order: {}", event.getPayload()
                .getTicketGuid(), event.getPayload().getOrderGuid());

        CreditCardAuthorization authorization =
                creditCardAuthorizationRepository
                        .findById(event.getPayload().getOrderGuid())
                        .orElseThrow(RuntimeException::new);

        authorization.setTicketCreated(true);

        if (!authorization.canBeAuthorized()) {
            return;
        }

        cardAuthorizationService.tryAuthorize(authorization);
    }
}
