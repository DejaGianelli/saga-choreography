package br.com.saga.kitchen.services;

import br.com.saga.kitchen.entities.Ticket;
import br.com.saga.kitchen.repositories.TicketRepository;
import br.com.saga.messaging.CreditCardAuthorizedEvent;
import br.com.saga.messaging.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class CreditCardAuthorizedEventProcessService {

    @Autowired
    TicketRepository ticketRepository;

    public void process(DomainEvent<CreditCardAuthorizedEvent> event) {

        Ticket ticket = ticketRepository
                .findByOrderGuid(event.getPayload().getOrderGuid())
                .orElseThrow(RuntimeException::new);

        ticket.setStatus(Ticket.Status.AWAITING_ACCEPTANCE);

        ticketRepository.save(ticket);
    }
}
