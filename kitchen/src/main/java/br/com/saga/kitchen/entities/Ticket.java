package br.com.saga.kitchen.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @Column(name = "guid", nullable = false)
    private UUID guid;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;
    @Column(name = "order_guid", nullable = false)
    private UUID orderGuid;

    public Ticket() {
        guid = UUID.randomUUID();
        status = Status.CREATE_PENDING;
    }

    private Ticket(UUID orderGuid) {
        this();
        this.orderGuid = orderGuid;
    }

    public static Ticket createNew(UUID orderGuid) {
        return new Ticket(orderGuid);
    }

    @Getter
    public enum Status {
        CREATE_PENDING,
        AWAITING_ACCEPTANCE,
        REJECTED;
    }
}
