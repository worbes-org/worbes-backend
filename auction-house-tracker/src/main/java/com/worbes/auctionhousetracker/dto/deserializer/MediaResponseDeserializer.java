package com.worbes.auctionhousetracker.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.worbes.auctionhousetracker.dto.response.MediaResponse;

import java.io.IOException;

public class MediaResponseDeserializer extends JsonDeserializer<MediaResponse> {

    @Override
    public MediaResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        MediaResponse media = new MediaResponse();

        JsonNode assets = node.get("assets");
        if (assets != null && assets.isArray() && !assets.isEmpty()) {
            String value = assets.get(0).get("value").asText();
            media.setIconUrl(value);
        }

        return media;
    }
}
