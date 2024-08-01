package com.adalytics.adalytics_backend.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document("campaigns")
public class Campaign {
    private String id;
    private String name;
    private String startDate;
    private String endDate;
    private Metric metric;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Metric {
        private BigDecimal clicks;
        private BigDecimal conversions;
        private BigDecimal engagements;
        private BigDecimal impressions;
        private BigDecimal interactions;
    }
}
