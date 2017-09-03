package shpe.consumer.controller;

import shpe.consumer.accessor.EventApiAccessorImpl;
import shpe.util.Timer;
import shpe.consumer.model.StubHubApiToken;
import shpe.consumer.model.StubHubEvent;
import shpe.util.IndexList;
import shpe.util.IndexListFactory;
import shpe.util.Sleeper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by jordan on 6/22/17.
 */
public class EventUpdateControllerImpl extends EventUpdateController {

    private static final int EXPECTED_MODIFIED_COUNT = 100000; //TODO make these configurable or set via constructor
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int MAX_EVENTS_PER_REQUEST = 2;
    private static final int MAX_REQUESTS_PER_MINUTE = 1;

    private final EventApiAccessorImpl eventRetriever;
    private final Timer timer;
    private final Sleeper threadSleeper;
    private final IndexListFactory indexListFactory;

    public EventUpdateControllerImpl(final EventApiAccessorImpl eventRetriever,
                                     final Timer timer, final Sleeper threadSleeper, final IndexListFactory indexListFactory) {
        this.eventRetriever = eventRetriever;
        this.timer = timer;
        this.threadSleeper = threadSleeper;
        this.indexListFactory = indexListFactory;
    }

    public Collection<StubHubEvent> update(StubHubApiToken accessToken) {
        try {
            int numEvents = eventRetriever.getNumEvents(accessToken);
            IndexList indexList = indexListFactory.getInstance(numEvents, MAX_EVENTS_PER_REQUEST, false);
            Iterator<Integer> indexListIterator = indexList.iterator();

            Collection<StubHubEvent> modifiedEvents = new ArrayList<StubHubEvent>(EXPECTED_MODIFIED_COUNT);
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

                modifiedEvents.addAll(eventRetriever.getEvents(accessToken, indexListIterator.next(), MAX_EVENTS_PER_REQUEST));
                requestsInMinute++;
            }

            return  modifiedEvents;

        } catch (Exception e) {
            throw new RuntimeException("Failed with Exception: \n", e);
        }
    }
}
