package com.worbes.adapter.blizzard.data.realm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RealmIndexResponse {

    private List<Realm> realms;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Realm {
        private Long id;
        private String slug;
    }
}
