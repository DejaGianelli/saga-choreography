package br.com.saga.accounting.repositories;

import br.com.saga.accounting.entities.CreditCardAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface CreditCardAuthorizationRepository extends JpaRepository<CreditCardAuthorization, UUID> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO credit_card_authorizations (id, ticket_created, status)
            VALUES (:id, true, 'PENDING')
            ON CONFLICT (id)
            DO UPDATE SET ticket_created = true
            """, nativeQuery = true)
    void upsertTicketCreated(@Param("id") UUID id);

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO credit_card_authorizations (id, customer_verified, status)
            VALUES (:id, true, 'PENDING')
            ON CONFLICT (id)
            DO UPDATE SET customer_verified = true
            """, nativeQuery = true)
    void upsertConsumerVerified(@Param("id") UUID id);

}
