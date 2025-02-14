package com.worbes.auctionhousetracker.oauth2;

public interface ApiCrawler {

    <T> T fetchData(String path, Class<T> responseType);
}
