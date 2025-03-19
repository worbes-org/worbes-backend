package com.worbes.auctionhousetracker.entity;

import com.worbes.auctionhousetracker.entity.enums.Region;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Realm {

    @Id
    private Long id;

    @Column(nullable = false)
    private Long connectedRealmId;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private Region region;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, String> name;

    @Column(length = 50, nullable = false, unique = true)
    private String slug;
}
