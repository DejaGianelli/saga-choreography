# Choreography-based SAGA

This project is a sample application designed to demonstrate the SAGA pattern 
commonly used in microservice architectures. The example is inspired by the 
fictional FTGO application from the book Microservices Patterns: With Examples 
in Java by Chris Richardson.

## Stack used

1. Java 17
2. Spring Framework
3. Kafka
4. Postgres

## SAGA's happy path

1. Order Service creates an Order in the APPROVAL_PENDING state and publishes 
an OrderCreated event.
2. Consumer Service consumes the OrderCreated event, verifies that the consumer
can place the order, and publishes a ConsumerVerified event.
3. Kitchen Service consumes the OrderCreated event, validates the Order, creates
a Ticket in a CREATE_PENDING state, and publishes the TicketCreated event.
4. Accounting Service consumes the OrderCreated event and creates a Credit-
CardAuthorization in a PENDING state.
5. Accounting Service consumes the TicketCreated and ConsumerVerified
events, charges the consumer’s credit card, and publishes the CreditCard-
Authorized event.
6. Kitchen Service consumes the CreditCardAuthorized event and changes the
state of the Ticket to AWAITING_ACCEPTANCE.
7. Order Service receives the CreditCardAuthorized events, changes the state of
the Order to APPROVED, and publishes an OrderApproved event.

## SAGA's failure scenario

1. Order Service creates an Order in the APPROVAL_PENDING state and publishes
an OrderCreated event.
2. Consumer Service consumes the OrderCreated event, verifies that the consumer
can place the order, and publishes a ConsumerVerified event.
3. Kitchen Service consumes the OrderCreated event, validates the Order, creates
a Ticket in a CREATE_PENDING state, and publishes the TicketCreated event.
4. Accounting Service consumes the OrderCreated event and creates a Credit-
CardAuthorization in a PENDING state.
5. Accounting Service consumes the TicketCreated and ConsumerVerified
events, charges the consumer’s credit card, and publishes a Credit Card
Authorization Failed event.
6. Kitchen Service consumes the Credit Card Authorization Failed event and
changes the state of the Ticket to REJECTED.
7. Order Service consumes the Credit Card Authorization Failed event and
changes the state of the Order to REJECTED.

Obs: To simulate Credit Card Authorization Failed event, set authorized flag 
to false in `./docker/wiremock/mappings/credit_card_authorization.json` file 