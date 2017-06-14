package CredentialManagement;

import App.StubHubApiScope;

/**
 * StubHubApiCredentials
 * - encapsulates credentials for the StubHub api (each scope)
 */
public class StubHubApiCredentials {

    private String consumerKey;
    private String consumerSecret;
    private String username;
    private String password;
    private StubHubApiScope scope;

    public StubHubApiCredentials(final String consumerKey, final String consumerSecret, final String username, final String password, final StubHubApiScope scope) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.username = username;
        this.password = password;
        this.scope = scope;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public StubHubApiScope getScope() {
        return scope;
    }
}
