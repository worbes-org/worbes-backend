package com.worbes.client.wow;

public interface BlizzardApiConfigProvider {
    String getId();

    String getSecret();

    String getTokenUrl();

    String getTokenBody();

    String getTokenKey();
}
