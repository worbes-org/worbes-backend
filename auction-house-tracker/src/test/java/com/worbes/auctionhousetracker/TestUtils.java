package com.worbes.auctionhousetracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import com.worbes.auctionhousetracker.entity.embeded.Language;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Language createDummyLanguage() {
        return new Language(
                "English", "Spanish", "Portuguese",
                "German", "English", "Spanish",
                "French", "Italian", "Russian",
                "Korean", "Chinese", "Chinese"
        );
    }

    public static ItemClassesIndexResponse createDummyItemClassesIndexResponse() {
        List<ItemClassesIndexResponse.ItemClass> itemClasses = generateDummyItemClasses();
        return new ItemClassesIndexResponse(itemClasses);
    }

    private static List<ItemClassesIndexResponse.ItemClass> generateDummyItemClasses() {
        return IntStream.range(0, 10) // You can adjust the range for more or fewer dummy items
                .mapToObj(i -> new ItemClassesIndexResponse.ItemClass(
                        (long) i,
                        createDummyLanguage() // Generate a dummy Language object for each ItemClass
                ))
                .collect(Collectors.toList());
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
