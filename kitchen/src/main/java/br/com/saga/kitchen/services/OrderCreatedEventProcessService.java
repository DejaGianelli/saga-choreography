package br.com.saga.kitchen.services;

import br.com.saga.kitchen.entities.Ticket;
import br.com.saga.kitchen.repositories.TicketRepository;
import br.com.saga.messaging.DomainEvent;
import br.com.saga.messaging.OrderCreatedEvent;
import br.com.saga.messaging.TicketCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static br.com.saga.messaging.EventType.TICKET_CREATED;

@Service
@Transactional
@Slf4j
public class OrderCreatedEventProcessService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    public void process(DomainEvent<OrderCreatedEvent> event) {

        Ticket ticket = Ticket.createNew(event.getPayload().getOrderGuid());
        Ticket saved = ticketRepository.save(ticket);

        String key = event.getPayload().getOrderGuid().toString();
        TicketCreatedEvent payload = TicketCreatedEvent.builder()
                .consumerGuid(event.getPayload().getConsumerGuid())
                .orderGuid(event.getPayload().getOrderGuid())
                .ticketGuid(saved.getGuid())
                .build();
        var ticketCreatedEvent = new DomainEvent<>(TICKET_CREATED.getKey(),
                payload);

        ProducerRecord<String, Object> record = new ProducerRecord<>(
                "order-events", key, ticketCreatedEvent);
        record.headers().add("EventType", TICKET_CREATED.keyBytes());

        kafkaTemplate.send(record).join();

        log.info("Ticket {} of customer {} created for order {}",
                saved.getGuid(), event.getPayload().getConsumerGuid(),
                ticket.getOrderGuid());
    }
}
