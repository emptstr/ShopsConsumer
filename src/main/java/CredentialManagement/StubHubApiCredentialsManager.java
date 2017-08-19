package CredentialManagement;

import App.StubHubApiScope;

/**
 * StubHubApiCredentialsManager
 * retrieves the login credentials for the provided scope
 * @author Jordan Gaston
 * @version 0.1.17
 */
public abstract class StubHubApiCredentialsManager {
    /**
     *
     * @param scope - the scope of the credentials to be retrieved
     * @return StubHubApiCredentials - the retrieved API credentials
     */
    public abstract StubHubApiCredentials getCredentials(StubHubApiScope scope);
}
