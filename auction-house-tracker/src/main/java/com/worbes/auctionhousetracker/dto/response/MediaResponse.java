package com.worbes.auctionhousetracker.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MediaResponse {

    private List<Asset> assets;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Asset {
        private String value;
    }
}
