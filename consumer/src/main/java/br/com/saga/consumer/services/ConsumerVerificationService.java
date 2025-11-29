package br.com.saga.consumer.services;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@Slf4j
@Transactional
public class ConsumerVerificationService {

    private final RestClient restClient;

    @Autowired
    public ConsumerVerificationService(RestClient restClient) {
        this.restClient = restClient;
    }

    public boolean tryVerify(@NonNull String document) {
        try {
            StopWatch watch = new StopWatch();
            watch.start();

            log.info("Making request to consumer verification service for " +
                    "customer {}", document);

            URI uri = UriComponentsBuilder.newInstance()
                    .scheme("http")
                    .host("localhost")
                    .port(8000)
                    .path("consumer/{document}/verification")
                    .buildAndExpand(document)
                    .toUri();

            ConsumerVerificationResponse response = restClient.post()
                    .uri(uri)
                    .contentType(APPLICATION_JSON)
                    .retrieve()
                    .body(ConsumerVerificationResponse.class);

            assert response != null;
            requireNonNull(response, "ConsumerVerificationResponse is null");

            watch.stop();

            log.info("Consumer verification finished for customer {} in {} ms",
                    document, watch.getTime());

            return response.getVerified();

        } catch (RestClientException ex) {
            log.warn("Error when attempting to verify customer {}", document);
            throw ex;
        } catch (Exception ex) {
            log.warn("Unexpected error when attempting to verify customer {}",
                    document);
            throw ex;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ConsumerVerificationResponse {
        private Boolean verified;
    }
}
