package EventManagement;

import App.StubHubEventRetrieverImpl;
import App.Timer;
import TokenManagement.StubHubApiToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jordan on 6/22/17.
 */
public class StubHubActiveEventUpdateManagerImpl extends StubHubActiveEventUpdateManager {

    private static final int EXPECTED_MODIFIED_COUNT = 100000;
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int MAX_EVENTS_PER_REQUEST = 2;
    private static final int MAX_REQUESTS_PER_MINUTE = 1;

    private final StubHubEventRetrieverImpl eventRetriever;
    private final StubHubDatabaseEventUpdateManager eventUpdateManager;
    private final Timer timer;
    private final Sleeper threadSleeper;
    private final IndexListFactory indexListFactory;

    public StubHubActiveEventUpdateManagerImpl(final StubHubEventRetrieverImpl eventRetriever, final StubHubDatabaseEventUpdateManager eventUpdateManager,
                                               final Timer timer, final Sleeper threadSleeper, final IndexListFactory indexListFactory) {
        this.eventRetriever = eventRetriever;
        this.eventUpdateManager = eventUpdateManager;
        this.timer = timer;
        this.threadSleeper = threadSleeper;
        this.indexListFactory = indexListFactory;
    }

    public Collection<StubHubEventID> update(StubHubApiToken accessToken) {
        try {
            int numEvents = eventRetriever.getNumEvents(accessToken);
            IndexList indexList = indexListFactory.getInstance(numEvents, MAX_EVENTS_PER_REQUEST, false);
            Iterator<Integer> indexListIterator = indexList.iterator();

            Collection<StubHubEventID> modifiedEvents = new ArrayList<StubHubEventID>(EXPECTED_MODIFIED_COUNT);
            int requestsInMinute = 0;
            timer.start();

            while (indexListIterator.hasNext()) {

                long elapsedSeconds = timer.getElapsedSecs();

                if (elapsedSeconds < SECONDS_IN_MINUTE) {
                    if (requestsInMinute >= MAX_REQUESTS_PER_MINUTE) {
                        threadSleeper.sleep((SECONDS_IN_MINUTE - elapsedSeconds + 1) * 1000);
                        timer.reset();
                        requestsInMinute = 0;
                    }
                } else {
                    timer.reset();
                    requestsInMinute = 0;
                }

                List<StubHubEvent> retrievedEvents = eventRetriever.getEvents(accessToken, indexListIterator.next(), MAX_EVENTS_PER_REQUEST);
                eventUpdateManager.update(retrievedEvents);
                List<StubHubEventID> eventIds = retrievedEvents.stream().map(StubHubEvent::getEventID).collect(Collectors.toList());
                modifiedEvents.addAll(eventIds);
                requestsInMinute++;
            }

            return  modifiedEvents;

        } catch (Exception e) {
            throw new RuntimeException("Failed with Exception: \n", e);
        }
    }
}
