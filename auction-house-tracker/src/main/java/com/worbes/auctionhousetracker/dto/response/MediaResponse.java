package com.worbes.auctionhousetracker.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.worbes.auctionhousetracker.dto.deserializer.MediaResponseDeserializer;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = MediaResponseDeserializer.class)
public class MediaResponse {

    private String iconUrl;
}
