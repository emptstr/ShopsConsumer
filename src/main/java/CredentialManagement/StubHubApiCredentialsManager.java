package CredentialManagement;

import App.StubHubApiScope;

/**
 * StubHubApiCredentialsManager
 * - retrieves the login credentials for the provided scope
 */
public interface StubHubApiCredentialsManager {
    StubHubApiCredentials getCredentials(StubHubApiScope scope);
}
