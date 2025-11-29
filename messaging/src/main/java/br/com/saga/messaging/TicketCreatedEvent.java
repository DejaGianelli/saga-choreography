package br.com.saga.messaging;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class TicketCreatedEvent {
    private UUID consumerGuid;
    private UUID orderGuid;
    private UUID ticketGuid;
}
