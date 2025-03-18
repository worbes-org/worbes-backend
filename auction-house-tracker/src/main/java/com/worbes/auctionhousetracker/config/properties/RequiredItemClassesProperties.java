package com.worbes.auctionhousetracker.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Set;

@ConfigurationProperties(prefix = "blizzard")
@Getter
@Setter
public class RequiredItemClassesProperties {
    private Map<Long, Set<Long>> requiredClasses;
}
