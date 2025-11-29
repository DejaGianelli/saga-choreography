package br.com.saga.accounting.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "credit_card_authorizations")
@NoArgsConstructor
public class CreditCardAuthorization {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "customer_guid")
    private UUID customerGuid;

    @Column(name = "customer_verified")
    private Boolean customerVerified = false;

    @Column(name = "ticket_created")
    private Boolean ticketCreated = false;

    public CreditCardAuthorization(UUID id) {
        this.status = Status.PENDING;
        this.id = id;
        this.customerVerified = false;
        this.ticketCreated = false;
    }

    public CreditCardAuthorization(UUID id, UUID customerGuid) {
        this(id);
        this.customerGuid = customerGuid;
    }

    public boolean canBeAuthorized() {
        return this.customerVerified && this.ticketCreated;
    }

    public void markTicketAsCreated() {
        this.ticketCreated = true;
    }

    @Getter
    public enum Status {
        PENDING,
        AUTHORIZATION_FAILED,
        AUTHORIZED;
    }
}
