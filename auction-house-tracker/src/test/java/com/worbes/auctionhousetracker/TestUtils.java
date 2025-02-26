package com.worbes.auctionhousetracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worbes.auctionhousetracker.dto.response.AuctionResponse;
import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import com.worbes.auctionhousetracker.entity.Auction;
import com.worbes.auctionhousetracker.entity.embeded.Language;
import com.worbes.auctionhousetracker.entity.enums.Region;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtils {
    private static final Random RANDOM = new Random();
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
     * 더미 Auction 엔티티를 생성하는 함수
     *
     * @param auctionId 경매 ID
     * @param itemId    아이템 ID
     * @param region    지역
     * @param active    활성화 상태
     * @return Auction 엔티티
     */
    public static Auction createDummyAuction(Long auctionId, Long itemId, Region region, Long realmId, boolean active) {
        AuctionResponse.AuctionDto dto = new AuctionResponse.AuctionDto();
        dto.setId(auctionId);
        dto.setItemId(itemId);
        dto.setQuantity(1);
        dto.setUnitPrice(1000);

        Auction auction = new Auction(dto, region, realmId);
        if (!active) {
            auction.end();
        }
        return auction;
    }

    /**
     * 랜덤한 Auction 엔티티 리스트를 생성하는 함수
     *
     * @param count  생성할 Auction 개수
     * @param region 지역
     * @param active 활성화 상태
     * @return Auction 엔티티 리스트
     */
    public static List<Auction> createDummyAuctions(int count, Region region, Long realmId, boolean active) {
        return IntStream.range(0, count)
                .mapToObj(i -> createDummyAuction(
                        (long) i + 1,
                        (long) i + 1000,
                        region,
                        realmId,
                        active
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

    public static <T> T loadJsonResource(String path, Class<T> valueType, Class<?> testClass) {
        try (InputStream is = testClass.getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + path);
            }
            return objectMapper.readValue(is, valueType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load json resource: " + path, e);
        }
    }
}
