package com.worbes.auctionhousetracker.oauth2;

public interface AccessTokenHandler {

    /**
     * 액세스 토큰이 존재하고, 만료기간이 지나지 않았으면 재사용한다.
     * 아닌 경우 블리자드 클라이언트 아이디와 시크릿을 사용해서 토큰을 가져온다.
     *
     * @return 액세스 토큰을 반환한다.
     */
    String getToken();

    /**
     * 토큰이 존재하고,만료 기간이 지나지 않았는지 확인한다.
     *
     * @return 토큰이 위의 자격을 만족하면 true, 아니면 false
     */
    boolean isTokenValid();
}
