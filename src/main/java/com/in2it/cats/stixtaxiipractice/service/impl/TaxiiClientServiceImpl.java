
package com.in2it.cats.stixtaxiipractice.service.impl;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import com.in2it.cats.stixtaxiipractice.client.TaxiiClient;
import com.in2it.cats.stixtaxiipractice.dto.TaxiiResponseDTO;
import com.in2it.cats.stixtaxiipractice.service.TaxiiClientService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import feign.FeignException;

@Service
@AllArgsConstructor
@Slf4j
public class TaxiiClientServiceImpl implements TaxiiClientService {

    private final TaxiiClient taxiiClient;
    private final ObjectMapper objectMapper;

    @Override
    public String discoverTaxiiServer() {
        return taxiiClient.discoverTaxiiServer();
    }

    @Override
    public String getApiRootInformation() {
        return taxiiClient.getApiRootInformation();
    }

    @Override
    public String getCollections() {
        return taxiiClient.getCollections();
    }

    @Override
    public String getObjects(String collectionId, int limit, String next) {
        return taxiiClient.getObjects(collectionId, limit, next);
    }

    @Override
    public TaxiiResponseDTO getAllObjects(String collectionId, int limit) {

        List<Map<String, Object>> allObjects = new ArrayList<>();

        String next = null;
        boolean more = true;

        while (more) {

            String response = getObjectsWithRetry(collectionId, limit, next);
            TaxiiResponseDTO taxiiResponse;

            try {
                taxiiResponse = objectMapper.readValue(response, TaxiiResponseDTO.class);
            } catch (JacksonException exception) {
                throw new IllegalStateException("Failed to parse TAXII response", exception);
            }

            if (taxiiResponse.getObjects() != null) {
                allObjects.addAll(taxiiResponse.getObjects());
            }

            more = taxiiResponse.isMore();

            if (more) {
                next = taxiiResponse.getNext();

                if (next == null || next.isBlank()) {
                    throw new IllegalStateException(
                            "TAXII response indicates more pages but next cursor is missing"
                    );
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();

                    throw new IllegalStateException(
                            "TAXII pagination interrupted", exception
                    );
                }
            }
        }

        TaxiiResponseDTO finalResponse = new TaxiiResponseDTO();
        finalResponse.setObjects(allObjects);
        finalResponse.setMore(false);
        finalResponse.setNext(null);

        return finalResponse;
    }



    private String getObjectsWithRetry(String collectionId, int limit, String next) {

        int maxRetries = 3;
        int attempt = 0;

        while (true) {
            try {
                return taxiiClient.getObjects(collectionId, limit, next);

            } catch (FeignException.TooManyRequests exception) {

                if (attempt >= maxRetries) {
                    log.error(
                            "TAXII API failed after {} retries. Collection ID: {}, Cursor: {}",
                            maxRetries,
                            collectionId,
                            next,
                            exception
                    );

                    throw exception;
                }

                long delay = 2000L * (1L << attempt);

                Collection<String> retryAfterValues = exception.responseHeaders()
                        .entrySet()
                        .stream()
                        .filter(entry -> "Retry-After".equalsIgnoreCase(entry.getKey()))
                        .map(Map.Entry::getValue)
                        .findFirst()
                        .orElse(null);

                if (retryAfterValues != null && !retryAfterValues.isEmpty()) {
                    try {
                        delay = Long.parseLong(
                                retryAfterValues.iterator().next()
                        ) * 1000L;
                    } catch (NumberFormatException ignored) {
                        log.warn("Could not parse Retry-After header. Using backoff.");
                    }
                }

                log.warn(
                        "TAXII rate limit reached. Attempt: {}. Retrying after {} ms",
                        attempt + 1,
                        delay
                );

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();

                    throw new IllegalStateException(
                            "TAXII pagination interrupted",
                            interruptedException
                    );
                }

                attempt++;

            } catch (FeignException exception) {

                log.error(
                        "TAXII API request failed. HTTP Status: {}, Collection ID: {}, Cursor: {}",
                        exception.status(),
                        collectionId,
                        next,
                        exception
                );

                throw exception;
            }
        }
    }



}
