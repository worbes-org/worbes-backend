package com.worbes.auctionhousetracker.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.worbes.auctionhousetracker.entity.embeded.Language;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RealmIndexResponse {

    private List<RealmIndexElement> realms;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RealmIndexElement {
        private Long id;
        private Language name;
        private String slug;
    }
}
