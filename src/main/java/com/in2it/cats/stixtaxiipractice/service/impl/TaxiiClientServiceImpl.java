
package com.in2it.cats.stixtaxiipractice.service.impl;

import com.in2it.cats.stixtaxiipractice.client.TaxiiClient;
import com.in2it.cats.stixtaxiipractice.service.TaxiiClientService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaxiiClientServiceImpl implements TaxiiClientService {

    private final TaxiiClient taxiiClient;

    @Override
    public String discoverTaxiiServer() {
        return taxiiClient.discoverTaxiiServer();
    }

    @Override
    public String getApiRootInformation() {
        return taxiiClient.getApiRootInformation();
    }
}
