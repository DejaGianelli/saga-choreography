package br.com.saga.kitchen.services;

import br.com.saga.kitchen.entities.Ticket;
import br.com.saga.kitchen.repositories.TicketRepository;
import br.com.saga.messaging.CreditCardAuthorizationFailedEvent;
import br.com.saga.messaging.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static br.com.saga.kitchen.entities.Ticket.Status.REJECTED;

@Service
@Transactional
@Slf4j
public class CreditCardAuthorizationFailedEventProcessService {

    private final TicketRepository ticketRepository;

    @Autowired
    public CreditCardAuthorizationFailedEventProcessService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public void process(DomainEvent<CreditCardAuthorizationFailedEvent> event) {

        Ticket ticket = ticketRepository
                .findByOrderGuid(event.getPayload().getOrderGuid())
                .orElseThrow(RuntimeException::new);

        ticket.setStatus(REJECTED);

        ticketRepository.save(ticket);
    }
}
