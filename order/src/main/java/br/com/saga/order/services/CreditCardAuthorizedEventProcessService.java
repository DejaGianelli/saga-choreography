package br.com.saga.order.services;

import br.com.saga.messaging.CreditCardAuthorizedEvent;
import br.com.saga.messaging.DomainEvent;
import br.com.saga.messaging.OrderApprovedEvent;
import br.com.saga.order.entities.Order;
import br.com.saga.order.repositories.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static br.com.saga.messaging.EventType.ORDER_APPROVED;
import static br.com.saga.messaging.KafkaHeader.EVENT_TYPE;
import static br.com.saga.order.entities.Order.Status.APPROVED;

@Service
@Slf4j
@Transactional
public class CreditCardAuthorizedEventProcessService {

    private final OrderRepository orderRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public CreditCardAuthorizedEventProcessService(OrderRepository orderRepository,
                                                   KafkaTemplate<String, Object> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void process(DomainEvent<CreditCardAuthorizedEvent> event) {

        Order order = orderRepository
                .findById(event.getPayload().getOrderGuid())
                .orElseThrow(RuntimeException::new);

        order.setStatus(APPROVED);

        String key = order.getGuid().toString();
        OrderApprovedEvent payload = OrderApprovedEvent.builder()
                .consumerGuid(order.getConsumerGuid())
                .orderGuid(order.getGuid())
                .build();
        var orderApprovedEvent = new DomainEvent<>(ORDER_APPROVED.getKey(), payload);

        ProducerRecord<String, Object> record = new ProducerRecord<>(
                "order-events", key, orderApprovedEvent);
        record.headers().add(EVENT_TYPE.getKey(), ORDER_APPROVED.keyBytes());

        kafkaTemplate.send(record).join();
    }
}
