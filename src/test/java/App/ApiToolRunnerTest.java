package App;

import CredentialManagement.StubHubApiCredentials;
import CredentialManagement.StubHubApiCredentialsManager;
import EventManagement.StubHubEvent;
import LoginManagement.StubHubApiLoginManager;
import LoginManagement.StubHubApiLoginResult;
import TokenManagement.StubHubApiAccessToken;
import TokenManagement.StubHubApiRefreshToken;
import TokenManagement.TokenManager;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
/**
 * ApiToolRunnerTest
 */
public class ApiToolRunnerTest {

    private static final String ACCESS_TOKEN_1 = "ACCESS_TOKEN_1";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    private static final int SECONDS_TILL_TOKEN_EXP_1 = 12345;
    private static final String ACCESS_TOKEN_2 = "ACCESS_TOKEN_2";
    private static final int SECONDS_TILL_TOKEN_EXP_2 = 12345;
    private static final String ACCESS_TOKEN_3 = "ACCESS_TOKEN_3";
    private static final int SECONDS_TILL_TOKEN_EXP_3 = 12345;
    private static final String ACCESS_TOKEN_4 = "ACCESS_TOKEN_4";
    private static final int SECONDS_TILL_TOKEN_EXP_4 = 12345;

    private ApiToolRunner toolRunner;

    @Mock
    private StubHubApiCredentialsManager credentialsManager;
    @Mock
    private TokenManager tokenManager;
    @Mock
    private StubHubApiLoginManager loginManager;
    @Mock
    private StubHubActiveRecordUpdateManager activeRecordUpdateManager;

    @Mock //TODO create actual events when the pojo is defined
    private StubHubEvent firstEvent, secondEvent;

    private StubHubApiAccessToken accessToken_1, accessToken_2, accessToken_3, accessToken_4;

    private StubHubApiRefreshToken refreshToken;

    private StubHubApiScope scope_1, scope_2, scope_3, scope_4;

    private List<StubHubEvent> events = Arrays.asList(firstEvent, secondEvent);

    @Mock
    private StubHubApiCredentials credentials;
    @Mock
    private StubHubApiLoginResult loginResult;


    @Before
    public void setUp() {

        scope_1 = StubHubApiScope.PROD;
        scope_2 = StubHubApiScope.PROD;
        scope_3 = StubHubApiScope.PROD;
        scope_4 = StubHubApiScope.PROD;

        accessToken_1 = new StubHubApiAccessToken(ACCESS_TOKEN_1, LocalDateTime.now(), scope_1);
        accessToken_2 = new StubHubApiAccessToken(ACCESS_TOKEN_2, LocalDateTime.now(), scope_2);
        accessToken_3 = new StubHubApiAccessToken(ACCESS_TOKEN_3, LocalDateTime.now(), scope_3);
        accessToken_4 = new StubHubApiAccessToken(ACCESS_TOKEN_4, LocalDateTime.now(), scope_4);

        refreshToken = new StubHubApiRefreshToken(REFRESH_TOKEN, scope_4);

        toolRunner = new ApiToolRunner(tokenManager, credentialsManager, loginManager, activeRecordUpdateManager);
    }

    @Test
    /**
     * testRunValidToken
     * - tests the logic when a valid access token is returned
     */
    public void testRunValidToken() {
        when(tokenManager.getAccessToken(scope_1)).thenReturn(accessToken_1);
        when(tokenManager.isExpired(accessToken_1)).thenReturn(false);
        testWorkflow(accessToken_1);
    }

    @Test
    /**
     * testRunValidToken
     * - tests the logic when no Token is returned
     */
    public void testRunNoToken() {
        when(tokenManager.getAccessToken(scope_2)).thenReturn(null);
        when(credentialsManager.getCredentials(scope_2)).thenReturn(credentials);
        when(loginManager.login(credentials)).thenReturn(loginResult);
        when(loginResult.getAccessTokenString()).thenReturn(ACCESS_TOKEN_1);
        when(loginResult.getRefreshTokenString()).thenReturn(REFRESH_TOKEN);
        when(loginResult.getSecondsToExp()).thenReturn(SECONDS_TILL_TOKEN_EXP_1);
        when(tokenManager.createTokens(ACCESS_TOKEN_1, REFRESH_TOKEN, SECONDS_TILL_TOKEN_EXP_2, scope_2)).thenReturn(accessToken_2);
        testWorkflow(accessToken_2);
    }

    @Test
    /**
     * testRunExpiredToken
     * - tests the logic when the retuned access token is expired
     *
     */
    public void testRunExpiredToken() {
        when(tokenManager.getAccessToken(scope_4)).thenReturn(accessToken_3);
        when(tokenManager.isExpired(accessToken_3)).thenReturn(true);
        when(credentialsManager.getCredentials(scope_4)).thenReturn(credentials);
        when(tokenManager.getRefreshToken(scope_4)).thenReturn(refreshToken);
        when(loginManager.refresh(credentials, refreshToken)).thenReturn(loginResult);
        when(loginResult.getAccessTokenString()).thenReturn(ACCESS_TOKEN_4);
        when(loginResult.getRefreshTokenString()).thenReturn(REFRESH_TOKEN);
        when(loginResult.getSecondsToExp()).thenReturn(SECONDS_TILL_TOKEN_EXP_4);
        when(tokenManager.createTokens(ACCESS_TOKEN_4, REFRESH_TOKEN, SECONDS_TILL_TOKEN_EXP_4, scope_4)).thenReturn(accessToken_4);
        testWorkflow(accessToken_4);
    }

    @Test(expected=RuntimeException.class)
    public void testRunRuntimeException(){
        when(tokenManager.getAccessToken(scope_3)).thenThrow(new IllegalArgumentException());
        toolRunner.run();
    }

    private void testWorkflow(final StubHubApiAccessToken accessToken) {
        toolRunner.run();
        verify(activeRecordUpdateManager).update(accessToken);
    }
}