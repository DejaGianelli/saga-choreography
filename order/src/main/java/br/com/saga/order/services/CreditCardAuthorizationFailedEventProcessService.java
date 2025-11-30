package br.com.saga.order.services;

import br.com.saga.messaging.CreditCardAuthorizationFailedEvent;
import br.com.saga.messaging.DomainEvent;
import br.com.saga.order.entities.Order;
import br.com.saga.order.repositories.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static br.com.saga.order.entities.Order.Status.REJECTED;

@Service
@Transactional
@Slf4j
public class CreditCardAuthorizationFailedEventProcessService {

    private final OrderRepository orderRepository;

    @Autowired
    public CreditCardAuthorizationFailedEventProcessService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void process(DomainEvent<CreditCardAuthorizationFailedEvent> event) {

        Order order = orderRepository
                .findById(event.getPayload().getOrderGuid())
                .orElseThrow(RuntimeException::new);

        order.setStatus(REJECTED);

        orderRepository.save(order);
    }
}
