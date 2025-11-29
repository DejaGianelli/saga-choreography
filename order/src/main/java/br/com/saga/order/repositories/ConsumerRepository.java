package br.com.saga.order.repositories;

import br.com.saga.order.entities.Consumer;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ConsumerRepository {
    public Consumer findById(UUID id) {
        // Always return same document for the sake of simplicity
        Consumer consumer = new Consumer();
        consumer.setId(id);
        consumer.setDocument("91126870064");
        return consumer;
    }
}
