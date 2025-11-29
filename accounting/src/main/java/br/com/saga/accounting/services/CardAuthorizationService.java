package br.com.saga.accounting.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@Transactional
public class CardAuthorizationService {

    private final boolean authorize = true;

    //Simulating delay of some external service verifying credit card
    public boolean tryAuthorize(UUID consumerGuid) {
        try {
            Thread.sleep(5000);
            if (authorize) {
                log.info("Consumer {} had its credit card verified", consumerGuid);
                return true;
            } else {
                log.info("Consumer {} had its credit card verification failed", consumerGuid);
                return false;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
