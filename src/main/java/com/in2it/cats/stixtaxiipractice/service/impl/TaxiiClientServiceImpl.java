
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
import java.util.List;
import java.util.Map;
import feign.FeignException;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;

@Service
@AllArgsConstructor
@Slf4j
public class TaxiiClientServiceImpl implements TaxiiClientService {

    private final TaxiiClient taxiiClient;
    private final ObjectMapper objectMapper;
    private final RetryRegistry retryRegistry;

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
        return getObjectsWithRetry(collectionId, limit, next);
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

        Retry retry = retryRegistry.retry("taxiiRetry");

        try {
            String response = retry.executeSupplier(() -> taxiiClient.getObjects(collectionId, limit, next));
            return response;

        } catch (FeignException exception) {
            log.error("TAXII API request failed after retries. HTTP Status: {}, Collection ID: {}, Cursor: {}", exception.status(), collectionId, next, exception);
            throw exception;
        }
    }
}
