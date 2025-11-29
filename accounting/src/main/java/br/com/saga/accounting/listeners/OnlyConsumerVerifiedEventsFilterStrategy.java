package br.com.saga.accounting.listeners;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.stereotype.Component;

import static br.com.saga.messaging.EventType.CONSUMER_VERIFIED;
import static br.com.saga.messaging.KafkaHeader.EVENT_TYPE;
import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class OnlyConsumerVerifiedEventsFilterStrategy
        implements RecordFilterStrategy<String, Object> {

    @Override
    public boolean filter(ConsumerRecord<String, Object> consumerRecord) {
        Iterable<Header> iterable = consumerRecord.headers()
                .headers(EVENT_TYPE.getKey());
        for (Header header : iterable) {
            if (!(new String(header.value(), UTF_8)
                    .equals(CONSUMER_VERIFIED.getKey()))) {
                return true; // Discard
            }
        }
        return false; // Keep
    }
}