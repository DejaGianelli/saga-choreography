package br.com.saga.order.services;

import br.com.saga.messaging.DomainEvent;
import br.com.saga.messaging.OrderCreatedEvent;
import br.com.saga.order.entities.Consumer;
import br.com.saga.order.entities.Order;
import br.com.saga.order.repositories.ConsumerRepository;
import br.com.saga.order.repositories.OrderRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static br.com.saga.messaging.EventType.ORDER_CREATED;
import static br.com.saga.messaging.KafkaHeader.EVENT_TYPE;

@Service
@Transactional
public class CreateOrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ConsumerRepository consumerRepository;

    @Autowired
    public CreateOrderService(OrderRepository orderRepository,
                              KafkaTemplate<String, Object> kafkaTemplate,
                              ConsumerRepository consumerRepository) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.consumerRepository = consumerRepository;
    }

    public void create(UUID consumerGuid) {

        Consumer consumer = consumerRepository.findById(consumerGuid);

        Order order = new Order(consumer.getId());
        order = orderRepository.save(order);

        String key = order.getGuid().toString();
        OrderCreatedEvent payload = OrderCreatedEvent.builder()
                .consumerGuid(order.getConsumerGuid())
                .consumerDocument(consumer.getDocument())
                .orderGuid(order.getGuid())
                .build();
        var event = new DomainEvent<>(ORDER_CREATED.getKey(), payload);

        ProducerRecord<String, Object> record = new ProducerRecord<>(
                "order-events", key, event);
        record.headers().add(EVENT_TYPE.getKey(), ORDER_CREATED.keyBytes());
        kafkaTemplate.send(record).join();
    }
}
