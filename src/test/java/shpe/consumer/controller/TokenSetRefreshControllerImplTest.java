package shpe.consumer.controller;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import shpe.consumer.accessor.LoginApiAccessor;
import shpe.consumer.model.StubHubApiLoginResult;
import shpe.consumer.model.StubHubApiToken;
import shpe.consumer.model.TokenSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TokenSetRefreshControllerImplTest {

    private TokenSetRefreshControllerImpl underTest;
    @Mock
    private TokenSetFactory tokenSetFactory;
    @Mock
    private LoginApiAccessor loginAccessor;

    private TokenSet expiredTokenSet;
    private TokenSet nonExpiredTokenSet;
    private StubHubApiLoginResult loginApiResponse;

    @Before
    public void setup(){

        StubHubApiToken expiredAccessToken = new StubHubApiToken("accessToken", LocalDateTime.now());
        StubHubApiToken nonexpiredAccessToken = new StubHubApiToken("accessToken2", LocalDateTime.now().plusMonths(6));
        StubHubApiToken refreshToken =  new StubHubApiToken("refreshToken", LocalDateTime.now());
        expiredTokenSet = new TokenSet(expiredAccessToken, refreshToken);
        nonExpiredTokenSet = new TokenSet(nonexpiredAccessToken, refreshToken);

        loginApiResponse = new StubHubApiLoginResult(nonexpiredAccessToken.getTokenString(), refreshToken.getTokenString(), 1000);

        underTest = new TokenSetRefreshControllerImpl(tokenSetFactory, loginAccessor);
    }

    @Test
    public void refreshExpiredTokenSet() throws Exception {
        when(loginAccessor.refresh(expiredTokenSet.getRefreshToken())).thenReturn(loginApiResponse);
        when(tokenSetFactory.createTokens(loginApiResponse.getAccessTokenString(),
                loginApiResponse.getRefreshTokenString(), loginApiResponse.getSecondsToExp())).thenReturn(nonExpiredTokenSet);
        TokenSet actual = underTest.refreshExpiredTokenSet(expiredTokenSet);
        assertEquals(nonExpiredTokenSet, actual);
    }
}