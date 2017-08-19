package LoginManagement;

import CredentialManagement.StubHubApiCredentials;
import TaskManagement.EntityFactory;
import TokenManagement.StubHubApiRefreshToken;
import com.google.inject.Inject;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

/**
 * Created by jordan on 6/14/17.
 */
public class StubApiLoginManagerImpl implements StubHubApiLoginManager {

    private static final String TARGET = "https://api.stubhub.com/login";
    private static final String AUTH_HEADER_PREFIX = "Basic %s";
    private static final String LOGIN_BODY_FORMAT_STRING = "grant_type=password&username=%s&password=%s&scope=%s";
    private static final String REFRESH_BODY_FORMAT_STRING = "grant_type=refresh_token&refresh_token=%s";

    private final Client client;
    private final StubHubApiTokenGenerator authTokenGenerator;
    private final EntityFactory<String> entityFactory;

    @Inject
    public StubApiLoginManagerImpl(final Client client, final StubHubApiTokenGenerator authTokenGenerator, EntityFactory<String> entityFactory){
        this.client = client;
        this.authTokenGenerator = authTokenGenerator;
        this.entityFactory = entityFactory;
    }

    public StubHubApiLoginResult login(final StubHubApiCredentials credentials) {
        WebTarget loginTarget = client.target(TARGET);
        Invocation.Builder loginRequestBuilder = loginTarget.request();
        loginRequestBuilder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
        String basicAuthToken = authTokenGenerator.generate(credentials.getConsumerKey(), credentials.getConsumerSecret());
        loginRequestBuilder.header(HttpHeaders.AUTHORIZATION, String.format(AUTH_HEADER_PREFIX, basicAuthToken));
        String bodyString = String.format(LOGIN_BODY_FORMAT_STRING, credentials.getUsername(), credentials.getPassword(), credentials.getScope().toString());
        Invocation loginInvocation = loginRequestBuilder.buildPost(entityFactory.getEntity(bodyString));
        return loginInvocation.invoke(StubHubApiLoginResult.class);
    }

    public StubHubApiLoginResult refresh(StubHubApiCredentials credentials, StubHubApiRefreshToken refreshToken) {
        WebTarget loginTarget = client.target(TARGET);
        Invocation.Builder loginRequestBuilder = loginTarget.request();
        loginRequestBuilder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
        String basicAuthToken = authTokenGenerator.generate(credentials.getConsumerKey(), credentials.getConsumerSecret());
        loginRequestBuilder.header(HttpHeaders.AUTHORIZATION, String.format(AUTH_HEADER_PREFIX, basicAuthToken));
        String bodyString = String.format(REFRESH_BODY_FORMAT_STRING, refreshToken.getTokenString());
        Invocation loginInvocation = loginRequestBuilder.buildPost(entityFactory.getEntity(bodyString));
        return loginInvocation.invoke(StubHubApiLoginResult.class);
    }
}
