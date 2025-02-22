package com.worbes.auctionhousetracker.entity;

import com.worbes.auctionhousetracker.dto.response.AuctionResponse;
import com.worbes.auctionhousetracker.entity.enums.Region;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auction_seq")
    @SequenceGenerator(name = "auction_seq", sequenceName = "auction_seq", allocationSize = 1000)
    private Long id;

    @Column(unique = true)
    private Long auctionId;

    private Long itemId;

    private Long quantity;

    private Long unitPrice;

    @Enumerated(EnumType.STRING)
    private Region region;

    @Column(nullable = false)
    private boolean active = true;


    @CreatedDate  // 🔥 최초 생성 시간 (자동 저장)
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate  // 🔥 마지막 업데이트 시간 (자동 갱신)
    private LocalDateTime updatedAt;

    public Auction(AuctionResponse.AuctionDto dto, Region region) {
        this.auctionId = dto.getId();
        this.itemId = dto.getItemId();
        this.quantity = dto.getQuantity();
        this.unitPrice = dto.getUnitPrice();
        this.region = region;
    }

    public void end() {
        this.active = false;
    }
}
