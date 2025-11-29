package br.com.saga.messaging;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class OrderCreatedEvent {
    private UUID consumerGuid;
    private String consumerDocument;
    private UUID orderGuid;
}
