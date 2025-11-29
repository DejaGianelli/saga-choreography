package br.com.saga.order.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @Column(name = "guid", nullable = false)
    private UUID guid;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "consumer_guid", nullable = false)
    private UUID consumerGuid;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public Order() {
        guid = UUID.randomUUID();
        status = Status.APPROVAL_PENDING;
        createdAt = LocalDateTime.now();
    }

    public Order(UUID consumerGuid) {
        this();
        this.consumerGuid = consumerGuid;
    }

    @Getter
    public enum Status {
        APPROVAL_PENDING,
        APPROVED;
    }
}
