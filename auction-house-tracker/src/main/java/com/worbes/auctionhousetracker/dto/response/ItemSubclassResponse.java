package com.worbes.auctionhousetracker.dto.response;

import com.worbes.auctionhousetracker.entity.embeded.Language;
import lombok.Data;

@Data
public class ItemSubclassResponse {

    private Long id;
    private Language name;
}
