package br.com.saga.accounting.repositories;

import br.com.saga.accounting.entities.CreditCardAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CreditCardAuthorizationRepository extends JpaRepository<CreditCardAuthorization, UUID> {


}
