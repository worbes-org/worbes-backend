package com.worbes.adapter.blizzard;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class WowHeadClient {

    private final RestClient restClient;

    public String getItemXml(long itemId) {
        String url = "https://www.wowhead.com/item=" + itemId + "&xml";

        return restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);
    }

    public String getItemHtmlTooltip(long itemId) {
        String xml = getItemXml(itemId);
        return extractHtmlTooltip(xml);
    }

    private String extractHtmlTooltip(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

            Element root = doc.getDocumentElement();
            return root.getElementsByTagName("htmlTooltip").item(0).getTextContent();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Wowhead XML", e);
        }
    }
}
