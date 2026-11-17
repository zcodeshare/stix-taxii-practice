package com.in2it.cats.stixtaxiipractice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "taxiiClient",
        url = "${taxii.server.url}"
)
public interface TaxiiClient {

    @GetMapping(
            value = "/taxii2/",
            produces = "application/taxii+json;version=2.1"
    )
    String discoverTaxiiServer();

    @GetMapping(
            value = "/api/v21/",
            produces = "application/taxii+json;version=2.1"
    )
    String getApiRootInformation();
}