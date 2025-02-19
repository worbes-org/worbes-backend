package com.worbes.auctionhousetracker.utils;

import com.worbes.auctionhousetracker.dto.response.AuctionResponse;
import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import com.worbes.auctionhousetracker.entity.embeded.Language;
import com.worbes.auctionhousetracker.entity.enums.TimeLeft;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestUtils {
    private static final Random RANDOM = new Random();

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

    /**
     * 랜덤한 AuctionDto 객체를 생성하는 함수
     *
     * @return AuctionDto 객체
     */
    public static AuctionResponse.AuctionDto createRandomAuctionDto(long count) {
        AuctionResponse.AuctionDto auctionDto = new AuctionResponse.AuctionDto();
        auctionDto.setId(count); // 1~1000 랜덤 ID
        auctionDto.setItemId(count + 1000); // 1000~11000 랜덤 Item ID
        auctionDto.setQuantity(RANDOM.nextLong(100) + 1); // 1~100 랜덤 수량
        auctionDto.setUnitPrice(RANDOM.nextLong(50000) + 1000); // 1000~51000 랜덤 단가
        auctionDto.setTimeLeft(getRandomTimeLeft().toString());
        return auctionDto;
    }

    /**
     * 랜덤한 AuctionDto 리스트를 생성하는 함수
     *
     * @param count 생성할 AuctionDto 개수
     * @return 더미 AuctionDto 리스트
     */
    public static List<AuctionResponse.AuctionDto> createRandomAuctionDtos(long count) {
        List<AuctionResponse.AuctionDto> auctionList = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            auctionList.add(createRandomAuctionDto(i));
        }
        return auctionList;
    }


    /**
     * 랜덤한 시간 남은 상태를 반환하는 함수
     *
     * @return 랜덤 TimeLeft 값
     */
    private static TimeLeft getRandomTimeLeft() {
        TimeLeft[] values = TimeLeft.values();
        return values[RANDOM.nextInt(values.length)];
    }

}
