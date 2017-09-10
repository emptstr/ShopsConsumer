package shpe.consumer.controller;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import shpe.consumer.controller.TokenSetController;
import shpe.consumer.controller.TokenSetControllerImpl;
import shpe.consumer.controller.TokenSetRefreshController;
import shpe.consumer.dao.TokenSetDao;
import shpe.consumer.generator.TokenSetGenerator;
import shpe.consumer.model.StubHubApiToken;
import shpe.consumer.model.TokenSet;
import shpe.consumer.predicate.IsTokenSetValidPredicate;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Jordan Gaston
 * @version 0.1.17 
 */
@RunWith(MockitoJUnitRunner.class)
public class TokenSetControllerImplTest {

    private static final String TOKEN_SET_PATH = "src/main/resources/token-set.ser";
    private static final String ACCESS_TOKEN_STRING = "ACCESS_TOKEN";
    private static final String REFRESHED_ACCESS_TOKEN_STRING = "REFRESHED_ACCESS_TOKEN";
    private static final String NEW_ACCESS_TOKEN_STRING = "NEW_ACCESS_TOKEN";
    private static final String REFRESH_TOKEN_STRING = "REFRESH_TOKEN";

    private TokenSetController accessTokenController;

    @Mock
    private TokenSetDao tokenSetDao;
    @Mock
    private TokenSetRefreshController tokenSetRefreshController;
    @Mock
    private TokenSetGenerator tokenSetGenerator;
    @Mock
    private IsTokenSetValidPredicate isTokenSetValid;

    private StubHubApiToken accessToken, refreshedAccessToken, newAccessToken;
    private StubHubApiToken refreshToken;
    private TokenSet tokenSet, refreshedTokenSet, newTokenSet;

    @Before
    public void setUp() {
        accessTokenController = new TokenSetControllerImpl(tokenSetDao, tokenSetRefreshController, tokenSetGenerator, isTokenSetValid);

        accessToken = new StubHubApiToken(ACCESS_TOKEN_STRING, LocalDateTime.now());
        refreshedAccessToken = new StubHubApiToken(REFRESHED_ACCESS_TOKEN_STRING, LocalDateTime.now());
        newAccessToken = new StubHubApiToken(NEW_ACCESS_TOKEN_STRING, LocalDateTime.now());
        refreshToken = new StubHubApiToken(REFRESH_TOKEN_STRING, LocalDateTime.now());

        tokenSet = new TokenSet(accessToken, refreshToken);
        refreshedTokenSet = new TokenSet(refreshedAccessToken, refreshToken);
        newTokenSet = new TokenSet(newAccessToken, refreshToken);
    }

    @Test
    public void testProvideAccessTokenIsSerializedNotExpired() throws IOException {
        when(tokenSetDao.fetchTokenSet(TOKEN_SET_PATH)).thenReturn(tokenSet);
        when(isTokenSetValid.test(tokenSet)).thenReturn(true);
        TokenSet actualResult = accessTokenController.retrieveTokenSet();
        assertEquals(tokenSet, actualResult);
    }

    @Test
    public void testProvideAccessTokenIsSerializedIsExpired() throws Exception {
        when(tokenSetDao.fetchTokenSet(TOKEN_SET_PATH)).thenReturn(tokenSet);
        when(isTokenSetValid.test(tokenSet)).thenReturn(false);
        when(tokenSetRefreshController.refreshExpiredTokenSet(tokenSet)).thenReturn(refreshedTokenSet);
        assertEquals(refreshedTokenSet, accessTokenController.retrieveTokenSet());
        verify(tokenSetDao).persistTokenSet(TOKEN_SET_PATH, refreshedTokenSet);
    }

    @Test
    public void testProvideAccessTokenDoesNotExist() throws Exception {
        when(tokenSetDao.fetchTokenSet(TOKEN_SET_PATH)).thenReturn(null);
        when(tokenSetGenerator.generateNewTokenSet()).thenReturn(newTokenSet);
        assertEquals(newTokenSet, accessTokenController.retrieveTokenSet());
        verify(tokenSetDao).persistTokenSet(TOKEN_SET_PATH, newTokenSet);
    }
}