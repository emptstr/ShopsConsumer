package App;

import TokenManagement.StubHubApiToken;

/**
 * StubHubActiveRecordUpdateManager
 * Manages the insertion and update for all active StubHub records (Events and Listings)
 */
public abstract class StubHubActiveRecordUpdateManager {

    /**
     * update
     * Updates all existing StubHub records (Events and Listings) and inserts those that do not exist.
     * @param accessToken - the access token is a user specific string required for accessing the StubHub Api's
     */
    public abstract void update(final StubHubApiToken accessToken);
}
