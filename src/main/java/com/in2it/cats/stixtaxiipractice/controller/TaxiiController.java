
package com.in2it.cats.stixtaxiipractice.controller;

import com.in2it.cats.stixtaxiipractice.service.TaxiiClientService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/taxii")
@AllArgsConstructor
public class TaxiiController {

    private final TaxiiClientService taxiiClientService;

    @GetMapping("/discovery")
    public String discoverTaxiiServer() {
        return taxiiClientService.discoverTaxiiServer();
    }

    @GetMapping("/api-root")
    public String getApiRootInformation() {
        return taxiiClientService.getApiRootInformation();
    }
}
