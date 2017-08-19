package App;

import EventManagement.StubHubActiveEventUpdateManager;
import EventManagement.StubHubEventID;
import TokenManagement.StubHubApiToken;

import java.util.Collection;

/**
 * StubHubActiveRecordUpdateManagerImpl
 * Manages the insertion and update for all active StubHub records (Events and Listings)
 */
public class StubHubActiveRecordUpdateManagerImpl extends StubHubActiveRecordUpdateManager {

    private final StubHubActiveEventUpdateManager activeEventUpdateManager;
    private final StubHubActiveListingUpdateManager activeListingUpdateManager;

    public StubHubActiveRecordUpdateManagerImpl(final StubHubActiveEventUpdateManager activeEventUpdateManager, final StubHubActiveListingUpdateManager activeListingUpdateManager) {

        this.activeEventUpdateManager = activeEventUpdateManager;
        this.activeListingUpdateManager = activeListingUpdateManager;
    }

    /**
     * update
     * Updates all existing StubHub records (Events and Listings) and inserts those that do not exist.
     * @param accessToken - the access token is a user specific string required for accessing the StubHub Api's
     */
    public void update(StubHubApiToken accessToken) {
        Collection<StubHubEventID> activeEventIDList = activeEventUpdateManager.update(accessToken);
        activeListingUpdateManager.update(accessToken, activeEventIDList);
    }
}
