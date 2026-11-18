
package com.in2it.cats.stixtaxiipractice.service;

import com.in2it.cats.stixtaxiipractice.dto.TaxiiResponseDTO;

public interface TaxiiClientService {

    String discoverTaxiiServer();
    String getApiRootInformation();
    String getCollections();
    String getObjects(String collectionId, int limit, String next);
    TaxiiResponseDTO getAllObjects(String collectionId, int limit);
}
