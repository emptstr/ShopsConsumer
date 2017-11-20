package shpe.consumer.accessor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import shpe.consumer.accessor.LoginApiAccessorImpl;
import shpe.consumer.deserializer.LoginApiResultDeserializer;
import shpe.consumer.factory.BasicAuthTokenFactory;
import shpe.consumer.factory.StringEntityFactory;
import shpe.consumer.model.StubHubApiCredentials;
import shpe.consumer.model.StubHubApiLoginResult;
import shpe.consumer.model.StubHubApiToken;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginApiAccessorImplTest {

    private static final String TARGET = "https://api.stubhub.com/login";
    private static final String BASIC_AUTH_TOKEN = "BASIC_AUTH_TOKEN";
    private static final String AUTH_HEADER_PREFIX = "Basic %s";
    private static final String LOGIN_BODY_FORMAT_STRING = "grant_type=password&username=%s&password=%s&scope=%s";
    private static final String REFRESH_BODY_FORMAT_STRING = "grant_type=refresh_token&refresh_token=%s";
    private static final String TO_BE_DESERIALIZED = "{\n" +
            "    \"access_token\": \"accesstoken\",\n" +
            "    \"refresh_token\": \"refreshtoken\",\n" +
            "    \"scope\": \"default\",\n" +
            "    \"token_type\": \"Bearer\",\n" +
            "    \"expires_in\": 1\n" +
            "}";

    @Mock
    private Client client;
    @Mock
    private WebTarget loginTarget;
    @Mock
    private Invocation.Builder requestBuilder;
    @Mock
    private BasicAuthTokenFactory authTokenGenerator;
    @Mock
    private Invocation loginInvocation;
    @Mock
    private StringEntityFactory entityFactory;

    private LoginApiAccessorImpl loginManager;

    private StubHubApiCredentials credentials;

    private StubHubApiLoginResult loginResult;

    private javax.ws.rs.client.Entity<java.lang.String> entity = Entity.text("Test");


    @Before
    public void setUp() {
        credentials = new StubHubApiCredentials("consumerkey", "consumersecret", "username", "password");
        loginResult = new StubHubApiLoginResult("accesstoken", "refreshtoken", 1);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.registerTypeAdapter(StubHubApiLoginResult.class, new LoginApiResultDeserializer()).create();
        loginManager = new LoginApiAccessorImpl( credentials, client, authTokenGenerator, entityFactory, gson);
    }

    @Test
    public void testLogin() {
        when(client.target(TARGET)).thenReturn(loginTarget);
        when(loginTarget.request()).thenReturn(requestBuilder);
        when(authTokenGenerator.generate(credentials.getConsumerKey(), credentials.getConsumerSecret())).thenReturn(BASIC_AUTH_TOKEN);
        String bodyString = String.format(LOGIN_BODY_FORMAT_STRING, "username", "password", "PROD");
        when(entityFactory.getEntity(bodyString, MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(entity);
        when(requestBuilder.buildPost(entity)).thenReturn(loginInvocation);
        when(loginInvocation.invoke(String.class)).thenReturn(TO_BE_DESERIALIZED);

        StubHubApiLoginResult actualLoginResult = loginManager.login();

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
        when(entityFactory.getEntity(bodyString, MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(entity);
        when(requestBuilder.buildPost(entity)).thenReturn(loginInvocation);
        when(loginInvocation.invoke(String.class)).thenReturn(TO_BE_DESERIALIZED);

        StubHubApiLoginResult actualLoginResult = loginManager.refresh(new StubHubApiToken("refreshtoken", org.joda.time.LocalDateTime.now()));

        verify(requestBuilder).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
        verify(requestBuilder).header(HttpHeaders.AUTHORIZATION, String.format(AUTH_HEADER_PREFIX, BASIC_AUTH_TOKEN));

        assertEquals(actualLoginResult.getAccessTokenString(), loginResult.getAccessTokenString());
        assertEquals(actualLoginResult.getRefreshTokenString(), loginResult.getRefreshTokenString());
        assertEquals(actualLoginResult.getSecondsToExp(), loginResult.getSecondsToExp());
    }
}