package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.builder.BlizzardApiParamsBuilder;
import com.worbes.auctionhousetracker.builder.BlizzardApiUrlBuilder;
import com.worbes.auctionhousetracker.dto.response.ItemResponse;
import com.worbes.auctionhousetracker.dto.response.MediaResponse;
import com.worbes.auctionhousetracker.entity.Item;
import com.worbes.auctionhousetracker.entity.enums.NamespaceType;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.exception.UnauthorizedException;
import com.worbes.auctionhousetracker.infrastructure.rest.RestApiClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.concurrent.CompletionException;

import static com.worbes.auctionhousetracker.TestUtils.loadJsonResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    private final Region region = Region.US;
    @Mock
    private RestApiClient restApiClient;
    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    @DisplayName("아이템 정보 수집 - 성공")
    void collectItemSuccess() {
        Region region = Region.US;
        ItemResponse itemResponse = loadJsonResource("/json/item-response.json", ItemResponse.class);
        MediaResponse mediaResponse = loadJsonResource("/json/media-response.json", MediaResponse.class);

        String itemPath = BlizzardApiUrlBuilder.builder(region).item(itemResponse.getId()).build();
        String mediaPath = BlizzardApiUrlBuilder.builder(region).media(itemResponse.getId()).build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(NamespaceType.STATIC).build();

        given(restApiClient.get(eq(itemPath), eq(params), eq(ItemResponse.class)))
                .willReturn(itemResponse);
        given(restApiClient.get(eq(mediaPath), eq(params), eq(MediaResponse.class)))
                .willReturn(mediaResponse);

        // when
        Item item = itemService.collectItemWithMedia(itemResponse.getId());

        // then
        assertThat(item).isNotNull();
        assertThat(item.getId()).isEqualTo(itemResponse.getId());
        assertThat(item.getName().getKo_KR()).isEqualTo(itemResponse.getName().getKo_KR());
        assertThat(item.getQuality()).isEqualTo(itemResponse.getQuality());
        assertThat(item.getIconUrl()).isEqualTo(mediaResponse.getAssets().get(0).getValue());

        // verify API calls
        verify(restApiClient).get(eq(itemPath), eq(params), eq(ItemResponse.class));
        verify(restApiClient).get(eq(mediaPath), eq(params), eq(MediaResponse.class));
    }

    @Test
    @DisplayName("아이템 정보 수집 - 아이템 API 실패")
    void collectItemWhenItemApiFails() {
        // given
        Long itemId = 210801L;
        String itemPath = BlizzardApiUrlBuilder.builder(region).item(itemId).build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(NamespaceType.STATIC).build();

        given(restApiClient.get(eq(itemPath), eq(params), eq(ItemResponse.class)))
                .willThrow(new UnauthorizedException());

        // when & then
        assertThatThrownBy(() -> itemService.collectItemWithMedia(itemId))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("아이템 정보 수집 - 미디어 API 실패")
    void collectItemWhenMediaApiFails() {
        // given
        ItemResponse itemResponse = loadJsonResource("/json/item-response.json", ItemResponse.class);

        String itemPath = BlizzardApiUrlBuilder.builder(region).item(itemResponse.getId()).build();
        String mediaPath = BlizzardApiUrlBuilder.builder(region).media(itemResponse.getId()).build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(NamespaceType.STATIC).build();

        given(restApiClient.get(eq(itemPath), eq(params), eq(ItemResponse.class)))
                .willReturn(itemResponse);
        given(restApiClient.get(eq(mediaPath), eq(params), eq(MediaResponse.class)))
                .willThrow(new UnauthorizedException());

        // when & then
        assertThatThrownBy(() -> itemService.collectItemWithMedia(itemResponse.getId()))
                .isInstanceOf(CompletionException.class);
    }
}
