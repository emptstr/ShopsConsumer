package shpe.consumer.accessor;

import com.google.inject.Inject;
import shpe.consumer.factory.BasicAuthTokenFactory;
import shpe.consumer.factory.EntityFactory;
import shpe.consumer.model.StubHubApiCredentials;
import shpe.consumer.model.StubHubApiLoginResult;
import shpe.consumer.model.StubHubApiToken;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public class LoginApiAccessorImpl implements LoginApiAccessor {

    private static final String TARGET = "https://api.stubhub.com/login";
    private static final String AUTH_HEADER_PREFIX = "Basic %s";
    private static final String LOGIN_BODY_FORMAT_STRING = "grant_type=password&username=%s&password=%s&scope=%s";
    private static final String REFRESH_BODY_FORMAT_STRING = "grant_type=refresh_token&refresh_token=%s";

    private final Client client;
    private final BasicAuthTokenFactory authTokenGenerator;
    private final StubHubApiCredentials credentials;
    private final EntityFactory<String> entityFactory;

    @Inject
    public LoginApiAccessorImpl(StubHubApiCredentials credentials, Client client,
                                BasicAuthTokenFactory authTokenGenerator, EntityFactory<String> entityFactory) {
        this.credentials = credentials;
        this.client = client;
        this.authTokenGenerator = authTokenGenerator;
        this.entityFactory = entityFactory;
    }

    public StubHubApiLoginResult login() {
        WebTarget loginTarget = client.target(TARGET);
        Invocation.Builder loginRequestBuilder = loginTarget.request();
        loginRequestBuilder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
        String basicAuthToken = authTokenGenerator.generate(credentials.getConsumerKey(), credentials.getConsumerSecret());
        loginRequestBuilder.header(HttpHeaders.AUTHORIZATION, String.format(AUTH_HEADER_PREFIX, basicAuthToken));
        String bodyString = String.format(LOGIN_BODY_FORMAT_STRING, credentials.getUsername(),
                credentials.getPassword(), "PROD");
        Invocation loginInvocation = loginRequestBuilder.buildPost(entityFactory.getEntity(bodyString,
                MediaType.APPLICATION_FORM_URLENCODED));
        return loginInvocation.invoke(StubHubApiLoginResult.class);
    }

    public StubHubApiLoginResult refresh(StubHubApiToken refreshToken) {
        WebTarget loginTarget = client.target(TARGET);
        Invocation.Builder loginRequestBuilder = loginTarget.request();
        loginRequestBuilder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
        String basicAuthToken = authTokenGenerator.generate(credentials.getConsumerKey(), credentials.getConsumerSecret());
        loginRequestBuilder.header(HttpHeaders.AUTHORIZATION, String.format(AUTH_HEADER_PREFIX, basicAuthToken));
        String bodyString = String.format(REFRESH_BODY_FORMAT_STRING, refreshToken.getTokenString());
        Invocation loginInvocation = loginRequestBuilder.buildPost(entityFactory.getEntity(bodyString,
                MediaType.APPLICATION_FORM_URLENCODED));
        return loginInvocation.invoke(StubHubApiLoginResult.class);
    }
}
