
package com.in2it.cats.stixtaxiipractice.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TaxiiResponseDTO {

    private boolean more;
    private String next;
    private List<Map<String, Object>> objects;
}
