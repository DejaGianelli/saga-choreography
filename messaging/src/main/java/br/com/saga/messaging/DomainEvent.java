package br.com.saga.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class DomainEvent<E> {
    private String type;
    private ZonedDateTime occurredAt;
    private E payload;

    public DomainEvent(String type, E payload) {
        this.type = type;
        this.occurredAt = ZonedDateTime.now();
        this.payload = payload;
    }
}
