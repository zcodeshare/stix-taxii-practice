package com.in2it.cats.stixtaxiipractice.controller;

import com.in2it.cats.stixtaxiipractice.dto.TaxiiResponseDTO;
import com.in2it.cats.stixtaxiipractice.service.TaxiiClientService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/collections")
    public String getCollections() {
        return taxiiClientService.getCollections();
    }

    @GetMapping("/collections/{collectionId}/objects")
    public String getObjects(@PathVariable String collectionId, @RequestParam(defaultValue = "10") int limit, @RequestParam(required = false) String next) {
        return taxiiClientService.getObjects(collectionId, limit, next);
    }


    @GetMapping("/collections/{collectionId}/objects/all")
    public TaxiiResponseDTO getAllObjects(@PathVariable String collectionId, @RequestParam(defaultValue = "10") int limit) {
        TaxiiResponseDTO response = taxiiClientService.getAllObjects(collectionId, limit);
        return response;
    }


}
