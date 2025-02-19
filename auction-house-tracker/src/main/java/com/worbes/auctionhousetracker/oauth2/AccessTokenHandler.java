package com.worbes.auctionhousetracker.oauth2;

public interface AccessTokenHandler {

    /**
     * 캐시에서 OAuth2 토큰을 조회하고 반환합니다.
     * 캐시에 저장된 토큰이 없으면 클라이언트 아이디와 시크릿을 기반으로 새로운 토큰을 발급받아 저장 후 반환합니다.
     *
     * @return 저장된 OAuth2 토큰 문자열
     */
    String get();

    /**
     * OAuth2 토큰을 강제로 갱신합니다.
     * 캐시에 기존 토큰이 존재하더라도 무조건 새로운 토큰을 발급받아 캐시에 저장합니다.
     *
     * @return 갱신된 OAuth2 토큰 문자열
     */
    String refresh();
}
