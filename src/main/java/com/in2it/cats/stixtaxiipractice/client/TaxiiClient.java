
package com.in2it.cats.stixtaxiipractice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "taxiiClient", url = "${taxii.server.url}")
public interface TaxiiClient {

    @GetMapping("/taxii2/")
    String discoverTaxiiServer();
}
