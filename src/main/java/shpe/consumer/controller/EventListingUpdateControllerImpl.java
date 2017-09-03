package shpe.consumer.controller;

import shpe.consumer.accessor.ListingApiAccessor;
import shpe.consumer.model.StubHubListing;
import shpe.util.Timer;
import shpe.consumer.model.StubHubApiToken;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import shpe.consumer.factory.StubHubListingCollectionFactory;
import shpe.consumer.model.StubHubEvent;
import shpe.util.Sleeper;

import java.util.ArrayList;
import java.util.Collection;


@RequiredArgsConstructor
public class EventListingUpdateControllerImpl extends EventListingUpdateController {

    private final ListingApiAccessor activeListingRetriever;
    private final StubHubListingCollectionFactory listingCollectionFactory;
    private final Timer timer;
    private final Sleeper threadSleeper;
    private final DateTimeFormatter dateTimeFormatter;
    private final int maxListingsPerRequest;
    private final int maxRequestsInMinute;

    @Override
    public Collection<StubHubEvent> update(StubHubApiToken accessToken, Collection<StubHubEvent> activeEventIDList) {

        DateTime retrievalDate = new DateTime(DateTime.now().toString(dateTimeFormatter));
        timer.start();
        int requestRowStart;
        int numberOfListingsInLastRequest;
        int requestsInLastMinute = 0;

        for (StubHubEvent stubHubEvent : activeEventIDList) {
            requestRowStart = 0;
            numberOfListingsInLastRequest = Integer.MAX_VALUE;

            Collection<StubHubListing> currentListingsForEvent = new ArrayList<>();
            do {
                boolean hasExceededMaxRequestsInMinute = hasExceededMaxRequestsInMin(requestsInLastMinute);
                if (hasExceededMaxRequestsInMinute || hasMinuteElapsed(timer.getElapsedSecs())) {
                    if (hasExceededMaxRequestsInMinute) {
                        threadSleeper.sleep(getSleepTime(timer.getElapsedSecs()));
                    }
                    timer.reset();
                    requestsInLastMinute = 0;
                } else if (!hasExceededMaxRequestsInMin(requestsInLastMinute)) {
                    Collection<StubHubListing> latestRetrievedListingsForEvent = activeListingRetriever.retrieve(accessToken,
                            stubHubEvent.getEventID(), requestRowStart, maxListingsPerRequest);
                    currentListingsForEvent.addAll(latestRetrievedListingsForEvent);
                    numberOfListingsInLastRequest = latestRetrievedListingsForEvent.size();
                    requestRowStart = requestRowStart + maxListingsPerRequest;
                    requestsInLastMinute++;
                }
            } while (eventHasMoreListings(numberOfListingsInLastRequest));
            updateEventListings(retrievalDate, stubHubEvent, currentListingsForEvent);
        }
        return new ArrayList<>(activeEventIDList);
    }

    private void updateEventListings(DateTime retrievalDate, StubHubEvent stubHubEvent, Collection<StubHubListing> currentListingsForEvent) {
        if (!currentListingsForEvent.isEmpty()) {
            stubHubEvent.addUpdatedListings(listingCollectionFactory.create(currentListingsForEvent, retrievalDate));
        }
    }

    private boolean eventHasMoreListings(int numberOfListingsInLastRequest) {
        return numberOfListingsInLastRequest >= maxListingsPerRequest;
    }

    private boolean hasMinuteElapsed(long elapsedSeconds) {
        return elapsedSeconds >= 60;
    }

    private long getSleepTime(long elapsedSeconds) {
        return (60 - elapsedSeconds + 1) * 1000;
    }

    private boolean hasExceededMaxRequestsInMin(int requestsInLastMinute) {
        return requestsInLastMinute >= maxRequestsInMinute;
    }
}
