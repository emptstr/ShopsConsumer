package LoginManagement;

import App.StubHubApiScope;
import CredentialManagement.StubHubApiCredentials;
import TaskManagement.EntityFactory;
import TokenManagement.StubHubApiRefreshToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jordan on 6/14/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class StubApiLoginManagerImplTest {

    private static final String TARGET = "https://api.stubhub.com/login";
    private static final String BASIC_AUTH_TOKEN = "BASIC_AUTH_TOKEN";
    private static final String AUTH_HEADER_PREFIX = "Basic %s";
    private static final String LOGIN_BODY_FORMAT_STRING = "grant_type=password&username=%s&password=%s&scope=%s";
    private static final String REFRESH_BODY_FORMAT_STRING = "grant_type=refresh_token&refresh_token=%s";

    @Mock
    private Client client;
    @Mock
    private WebTarget loginTarget;
    @Mock
    private Invocation.Builder requestBuilder;
    @Mock
    private StubHubApiTokenGenerator authTokenGenerator;
    @Mock
    private Invocation loginInvocation;
    @Mock
    private EntityFactory<String> entityFactory;

    private StubApiLoginManagerImpl loginManager;

    private StubHubApiCredentials credentials;

    private StubHubApiLoginResult loginResult;

    private javax.ws.rs.client.Entity<java.lang.String> entity = Entity.text("Test");


    @Before
    public void setUp() {
        loginManager = new StubApiLoginManagerImpl(client, authTokenGenerator, entityFactory);

        credentials = new StubHubApiCredentials("consumerkey", "consumersecret", "username", "password", StubHubApiScope.PROD);
        loginResult = new StubHubApiLoginResult("accesstoken", "refreshtoken", 1);
    }

    @Test
    public void testLogin() {
        when(client.target(TARGET)).thenReturn(loginTarget);
        when(loginTarget.request()).thenReturn(requestBuilder);
        when(authTokenGenerator.generate(credentials.getConsumerKey(), credentials.getConsumerSecret())).thenReturn(BASIC_AUTH_TOKEN);
        String bodyString = String.format(LOGIN_BODY_FORMAT_STRING, "username", "password", StubHubApiScope.PROD.toString());
        when(entityFactory.getEntity(bodyString)).thenReturn(entity);
        when(requestBuilder.buildPost(entity)).thenReturn(loginInvocation);
        when(loginInvocation.invoke(StubHubApiLoginResult.class)).thenReturn(loginResult);

        StubHubApiLoginResult actualLoginResult = loginManager.login(credentials);

        verify(requestBuilder).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
        verify(requestBuilder).header(HttpHeaders.AUTHORIZATION, String.format(AUTH_HEADER_PREFIX, BASIC_AUTH_TOKEN));

        assertEquals(actualLoginResult.getAccessTokenString(), loginResult.getAccessTokenString());
        assertEquals(actualLoginResult.getRefreshTokenString(), loginResult.getRefreshTokenString());
        assertEquals(actualLoginResult.getSecondsToExp(), loginResult.getSecondsToExp());
    }

    @Test
    public void testRefresh() {
        when(client.target(TARGET)).thenReturn(loginTarget);
        when(loginTarget.request()).thenReturn(requestBuilder);
        when(authTokenGenerator.generate(credentials.getConsumerKey(), credentials.getConsumerSecret())).thenReturn(BASIC_AUTH_TOKEN);
        String bodyString = String.format(REFRESH_BODY_FORMAT_STRING, "refreshtoken");
        when(entityFactory.getEntity(bodyString)).thenReturn(entity);
        when(requestBuilder.buildPost(entity)).thenReturn(loginInvocation);
        when(loginInvocation.invoke(StubHubApiLoginResult.class)).thenReturn(loginResult);

        StubHubApiLoginResult actualLoginResult = loginManager.refresh(credentials, new StubHubApiRefreshToken("refreshtoken", StubHubApiScope.PROD));

        verify(requestBuilder).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
        verify(requestBuilder).header(HttpHeaders.AUTHORIZATION, String.format(AUTH_HEADER_PREFIX, BASIC_AUTH_TOKEN));

        assertEquals(actualLoginResult.getAccessTokenString(), loginResult.getAccessTokenString());
        assertEquals(actualLoginResult.getRefreshTokenString(), loginResult.getRefreshTokenString());
        assertEquals(actualLoginResult.getSecondsToExp(), loginResult.getSecondsToExp());
    }
}