package shpe.consumer.controller;

import lombok.RequiredArgsConstructor;
import shpe.consumer.dao.StubHubEventDao;
import shpe.consumer.model.StubHubApiToken;
import shpe.consumer.model.StubHubEvent;

import java.util.Collection;

/**
 * ActiveEventUpdateControllerImpl
 * Manages the insertion and update for all active StubHub records (Events and Listings)
 */
@RequiredArgsConstructor
public class ActiveEventUpdateControllerImpl extends ActiveEventUpdateController {

    private final EventUpdateController activeEventUpdateManager;
    private final EventListingUpdateController activeListingUpdateManager;
    private final StubHubEventDao eventDao;

    /**
     * update
     * Updates all existing StubHub records (Events and Listings) and inserts those that do not exist.
     * @param accessToken - the access token is a user specific string required for accessing the StubHub Api's
     */
    public void update(StubHubApiToken accessToken) {
        Collection<StubHubEvent> activeEventIDList = activeEventUpdateManager.update(accessToken);
        Collection<StubHubEvent> eventsWithUpdatedListings = activeListingUpdateManager.update(accessToken, activeEventIDList);
        eventsWithUpdatedListings.forEach(eventDao::saveEvent);
    }
}
