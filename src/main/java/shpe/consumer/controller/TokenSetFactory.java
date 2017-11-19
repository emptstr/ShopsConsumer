package shpe.consumer.controller;

import org.joda.time.LocalDateTime;
import shpe.consumer.model.StubHubApiToken;
import shpe.consumer.model.TokenSet;

public class TokenSetFactory {
    public TokenSet createTokens(String accessTokenString, String refreshTokenString, int secondsToExp) {
        StubHubApiToken accessToken = new StubHubApiToken(accessTokenString, LocalDateTime.now().plusSeconds(secondsToExp));
        StubHubApiToken refreshToken = new StubHubApiToken(refreshTokenString, LocalDateTime.now().plusSeconds(Integer.MAX_VALUE));
        return new TokenSet(accessToken, refreshToken);
    }
}
