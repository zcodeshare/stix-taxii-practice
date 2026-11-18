package com.in2it.cats.stixtaxiipractice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "taxiiClient", url = "${taxii.server.url}")
public interface TaxiiClient {

    @GetMapping(value = "/taxii2/", produces = "application/taxii+json;version=2.1")
    String discoverTaxiiServer();

    @GetMapping(value = "/api/v21/", produces = "application/taxii+json;version=2.1")
    String getApiRootInformation();

    @GetMapping(value = "/api/v21/collections/", produces = "application/taxii+json;version=2.1")
    String getCollections();

    @GetMapping(value = "/api/v21/collections/{collectionId}/objects/", produces = "application/taxii+json;version=2.1")
    String getObjects(@PathVariable("collectionId") String collectionId, @RequestParam("limit") int limit, @RequestParam(value = "next", required = false) String next);

}