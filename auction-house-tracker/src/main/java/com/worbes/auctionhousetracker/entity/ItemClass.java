package com.worbes.auctionhousetracker.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "item_class",
        uniqueConstraints = @UniqueConstraint(columnNames = "item_class_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ItemClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_class_id", nullable = false)
    private Long itemClassId;

    @Column(nullable = false, length = 50)
    private String name; // 아이템 클래스 이름 (예: 무기, 방어구)

    @Column(nullable = false, length = 10)
    private String locale;
}
