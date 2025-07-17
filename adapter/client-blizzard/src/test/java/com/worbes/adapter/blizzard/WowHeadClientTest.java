package com.worbes.adapter.blizzard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WowHeadClientTest {

    @Autowired
    private WowHeadClient wowHeadClient;

    @Test
    void get() {
        String itemXml = wowHeadClient.getItemXml(210930);
        System.out.println(itemXml);
    }
}
