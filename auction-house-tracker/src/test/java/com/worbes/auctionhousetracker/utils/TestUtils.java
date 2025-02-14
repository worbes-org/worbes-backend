package com.worbes.auctionhousetracker.utils;

import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import com.worbes.auctionhousetracker.entity.embeded.Language;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestUtils {
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

}
