package shpe.consumer.controller;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shpe.consumer.accessor.ListingApiAccessor;
import shpe.consumer.model.StubHubListing;
import shpe.util.SleeperImpl;
import shpe.util.Timer;
import shpe.consumer.model.StubHubApiToken;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import shpe.consumer.factory.StubHubListingCollectionFactory;
import shpe.consumer.model.StubHubEvent;
import shpe.util.Sleeper;
import shpe.util.TimerImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;


@RequiredArgsConstructor
public class EventListingUpdateControllerImpl extends EventListingUpdateController {

    private static Logger logger = LoggerFactory.getLogger(EventUpdateControllerImpl.class);

    private final ListingApiAccessor activeListingRetriever;
    private final StubHubListingCollectionFactory listingCollectionFactory;
    private final Timer requestTimer;
    private final Timer timeOutTimer;
    private final Sleeper threadSleeper;
    private final DateTimeFormatter dateTimeFormatter;
    private final int maxListingsPerRequest;
    private final int maxRequestsInMinute;
    private final int timeoutDurationInSeconds;
    // metrics below
    private final Histogram requestFailureRatioMetric, timeSpentSleepingMetric;


    public EventListingUpdateControllerImpl(ListingApiAccessor activeListingRetriever, int maxListingsPerRequest,
                                            int maxRequestsInMinute, int timeoutDurationInSeconds, MetricRegistry metricRegistry) {
        this.activeListingRetriever = activeListingRetriever;
        this.maxListingsPerRequest = maxListingsPerRequest;
        this.maxRequestsInMinute = maxRequestsInMinute;
        this.timeoutDurationInSeconds = timeoutDurationInSeconds;
        listingCollectionFactory = new StubHubListingCollectionFactory();
        requestTimer = new TimerImpl();
        timeOutTimer = new TimerImpl();
        threadSleeper = new SleeperImpl();
        dateTimeFormatter = ISODateTimeFormat.basicDate();
        this.requestFailureRatioMetric = metricRegistry.histogram("LISTING_API_REQUEST_FAILURE_RATIO");
        this.timeSpentSleepingMetric = metricRegistry.histogram("LISTING_API_TIME_SPENT_SLEEPING");
    }

    @Override
    public Collection<StubHubEvent> update(StubHubApiToken accessToken, Collection<StubHubEvent> activeEventIDList) {
        logger.info("Starting retrieving listings for %d events...", activeEventIDList.size());
        DateTime retrievalDate = new DateTime(DateTime.now().toString(dateTimeFormatter));
        requestTimer.start();
        timeOutTimer.start();
        int requestRowStart;
        int numberOfListingsInLastRequest;
        int requestsInLastMinute = 0;

        Collection<StubHubEvent> modifiedEvents = new LinkedList<>();

        for (StubHubEvent stubHubEvent : activeEventIDList) {
            logger.info("Starting retrieving listings for event: ...", activeEventIDList.size());
            requestRowStart = 0;
            numberOfListingsInLastRequest = Integer.MAX_VALUE;

            Collection<StubHubListing> currentListingsForEvent = new ArrayList<>();
            do {
                boolean hasExceededMaxRequestsInMinute = hasExceededMaxRequestsInMin(requestsInLastMinute);
                if (hasExceededMaxRequestsInMinute || hasMinuteElapsed(requestTimer.getElapsedSecs())) {
                    if (hasExceededMaxRequestsInMinute) {
                        long sleepTime = getSleepTime(requestTimer.getElapsedSecs());
                        timeSpentSleepingMetric.update(sleepTime);
                        threadSleeper.sleep(sleepTime);
                    }
                    requestTimer.reset();
                    requestsInLastMinute = 0;
                } else if (!hasExceededMaxRequestsInMin(requestsInLastMinute)) {
                    Optional<Collection<StubHubListing>> latestRetrievedListingsForEventOptional = getListingsForEvent(accessToken,
                            requestRowStart, stubHubEvent);
                    if (latestRetrievedListingsForEventOptional.isPresent()) {
                        Collection<StubHubListing> latestRetrievedListingsForEvent = latestRetrievedListingsForEventOptional.get();
                        currentListingsForEvent.addAll(latestRetrievedListingsForEvent);
                        numberOfListingsInLastRequest = latestRetrievedListingsForEvent.size();
                    }
                    requestsInLastMinute++;
                    requestRowStart = requestRowStart + maxListingsPerRequest;
                }
            } while (eventHasMoreListings(numberOfListingsInLastRequest));
            logger.info("Finished retrieving %d listings for event...", currentListingsForEvent.size(), stubHubEvent
                    .getEventID().getEventID());
            updateEventListings(retrievalDate, stubHubEvent, currentListingsForEvent);
            modifiedEvents.add(stubHubEvent);
            if (timeoutDurationInSeconds <= timeOutTimer.getElapsedSecs()) {
                return modifiedEvents;
            }
        }
        logger.info("Finished retrieving listings for %d events...", modifiedEvents.size());
        return modifiedEvents;
    }

    private Optional<Collection<StubHubListing>> getListingsForEvent(StubHubApiToken accessToken, int requestRowStart, StubHubEvent stubHubEvent) {
        try{
            Optional<Collection<StubHubListing>> retrievedListings = Optional.of(activeListingRetriever.retrieve(accessToken,
                    stubHubEvent.getEventID(), requestRowStart, maxListingsPerRequest));
            requestFailureRatioMetric.update(0);

            return retrievedListings;
        } catch (Exception e) {
            String failureString = String.format("Failed while retrieving listings at row: &d," +
                            " while a max request size of: %d, for event: %s", requestRowStart,
                    maxListingsPerRequest, stubHubEvent.getEventID().getEventID());
            logger.warn(failureString);
            requestFailureRatioMetric.update(1);
        }
        return Optional.empty();
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
