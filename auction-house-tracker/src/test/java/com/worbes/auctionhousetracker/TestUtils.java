package com.worbes.auctionhousetracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worbes.auctionhousetracker.entity.embeded.Translation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Translation createDummyLanguage() {
        return new Translation(
                "English", "Spanish", "Portuguese",
                "German", "English", "Spanish",
                "French", "Italian", "Russian",
                "Korean", "Chinese", "Chinese"
        );
    }

    public static <T> T loadJsonResource(String path, Class<T> valueType) {
        try (InputStream is = TestUtils.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + path);
            }
            return objectMapper.readValue(is, valueType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load json resource: " + path, e);
        }
    }
}
