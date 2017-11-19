package shpe.consumer.controller;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ActiveEventUpdateControllerImpl.class);

    private final EventUpdateController activeEventUpdateManager;
    private final EventListingUpdateController activeListingUpdateManager;
    private final StubHubEventDao eventDao;
    // metrics below
    private final Timer eventUpdateTimer, eventListingUpdateTimer;
    private final Histogram numberOfRetrievedEventHist, numberOfUpdatedEventsHist;

    public ActiveEventUpdateControllerImpl(EventUpdateController activeEventUpdateManager, EventListingUpdateController eventListingUpdateController,
                                           StubHubEventDao eventDao, MetricRegistry metricRegistry){
        this.activeEventUpdateManager = activeEventUpdateManager;
        this.activeListingUpdateManager = eventListingUpdateController;
        this.eventDao = eventDao;
        this.eventUpdateTimer = metricRegistry.timer("EVENT_API_TOTAL_LATENCY");
        this.eventListingUpdateTimer = metricRegistry.timer("EVENT_API_TOTAL_LATENCY");
        this.numberOfRetrievedEventHist = metricRegistry.histogram("NUM_EVENTS_RETRIEVED");
        this.numberOfUpdatedEventsHist = metricRegistry.histogram("NUM_EVENTS_UPDATED");
    }

    /**
     * update
     * Updates all existing StubHub records (Events and Listings) and inserts those that do not exist.
     * @param accessToken - the access token is a user specific string required for accessing the StubHub Api's
     */
    public void update(StubHubApiToken accessToken) {
        logger.info(String.format("Started updating events using access token: %s", accessToken));
        Timer.Context runningContext = eventUpdateTimer.time();
        Collection<StubHubEvent> activeEventIDList = activeEventUpdateManager.update(accessToken);
        numberOfRetrievedEventHist.update(activeEventIDList.size());
        runningContext.stop();
        runningContext = eventListingUpdateTimer.time();
        Collection<StubHubEvent> eventsWithUpdatedListings = activeListingUpdateManager.update(accessToken, activeEventIDList);
        numberOfUpdatedEventsHist.update(eventsWithUpdatedListings.size());
        runningContext.stop();
        eventsWithUpdatedListings.forEach(eventDao::saveEvent);
        logger.info(String.format("Finished updating events using access token: %s", accessToken));
    }
}
