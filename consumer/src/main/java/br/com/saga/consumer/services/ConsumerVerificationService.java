package br.com.saga.consumer.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class ConsumerVerificationService {

    public boolean verify() {
        log.info("Verifying consumer details");

        try {
            Thread.sleep(5000); // Some fake delay to simulate long-running process
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        log.info("Consumer details verified");
        return true;
    }
}
