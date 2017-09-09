package shpe.consumer.controller;

import shpe.consumer.accessor.ListingApiAccessor;
import shpe.consumer.model.*;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import shpe.consumer.factory.StubHubListingCollectionFactory;
import shpe.util.Sleeper;
import shpe.util.Timer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static shpe.consumer.model.StubHubEvent.*;

@RunWith(MockitoJUnitRunner.class)
public class EventListingUpdateControllerImplTest {

    private static final int ROW_START_1 = 0;
    private static final int ROW_START_2 = 2;
    private static final int MAX_LISTINGS_PER_REQUEST = 2;
    private static final int MAX_REQUESTS_IN_MINUTE = 2;

    private EventListingUpdateControllerImpl activeListingUpdateManager;
    @Mock
    private ListingApiAccessor activeListingRetriever;
    @Mock
    private StubHubListingCollectionFactory listingCollectionFactory;
    private DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.basicDate();
    @Mock
    private Timer requestTimer, timeoutTimer;
    @Mock
    private Sleeper threadSleeper;
    @Mock
    private StubHubEvent event1, event2, event3;
    @Mock
    private StubHubListing listing1, listing2, listing3, listing4;

    private StubHubApiToken accessToken;
    private StubHubEventID eventId1, eventId2, eventId3;
    private Collection<StubHubEvent> eventsToRetrieveListingsFor;

    private Collection<StubHubListing> event1Listings1, event1Listings2, event2Listings1, event3Listings1;
    private Collection<StubHubListing> allEvent1Listings, allEvent2Listings;
    private DateTime dateOfListingRetrievals;
    private StubHubListingCollection event1ListingCollection, event2ListingCollection, event3ListingCollection;

    @Before
    public void setup(){
        accessToken = new StubHubApiToken("API_TOKEN", LocalDateTime.now());

        eventId1 = new StubHubEventID("event_id1");
        eventId2 = new StubHubEventID("event_id2");
        eventId3 = new StubHubEventID("event_id3");

        eventsToRetrieveListingsFor = Arrays.asList(event1, event2, event3);

        event1Listings1 = Arrays.asList(listing1, listing2);
        event1Listings2 = Arrays.asList(listing3);
        event2Listings1 = Arrays.asList(listing4);
        event3Listings1 = Arrays.asList();

        allEvent1Listings = new ArrayList<>(event1Listings1);
        allEvent1Listings.addAll(event1Listings2);
        allEvent2Listings = new ArrayList<>(event2Listings1);

        dateOfListingRetrievals = new DateTime(DateTime.now().toString(dateTimeFormatter));

        event1ListingCollection = new StubHubListingCollection(dateOfListingRetrievals, allEvent1Listings);
        event2ListingCollection = new StubHubListingCollection(dateOfListingRetrievals, allEvent2Listings);
        event3ListingCollection = new StubHubListingCollection(dateOfListingRetrievals, Collections.emptyList());
    }

    @Test
    public void shouldUpdateActiveListings() throws Exception {

        activeListingUpdateManager = new EventListingUpdateControllerImpl(activeListingRetriever,
                listingCollectionFactory, requestTimer, timeoutTimer, threadSleeper, dateTimeFormatter
                ,MAX_LISTINGS_PER_REQUEST, MAX_REQUESTS_IN_MINUTE, 10, null, null);

        when(event1.getEventID()).thenReturn(eventId1);
        when(event2.getEventID()).thenReturn(eventId2);
        when(event3.getEventID()).thenReturn(eventId3);
        when(activeListingRetriever.retrieve(accessToken, eventId1, ROW_START_1, MAX_LISTINGS_PER_REQUEST)).thenReturn(event1Listings1);
        when(activeListingRetriever.retrieve(accessToken,eventId1, ROW_START_2, MAX_LISTINGS_PER_REQUEST)).thenReturn(event1Listings2);
        when(activeListingRetriever.retrieve(accessToken, eventId2, ROW_START_1, MAX_LISTINGS_PER_REQUEST)).thenReturn(event2Listings1);
        when(activeListingRetriever.retrieve(accessToken, eventId3, ROW_START_1, MAX_LISTINGS_PER_REQUEST)).thenReturn(event3Listings1);
        when(listingCollectionFactory.create(allEvent1Listings, dateOfListingRetrievals)).thenReturn(event1ListingCollection);
        when(listingCollectionFactory.create(allEvent2Listings, dateOfListingRetrievals)).thenReturn(event2ListingCollection);
        when(requestTimer.getElapsedSecs()).thenReturn(30L).thenReturn(55L).thenReturn(59L).thenReturn(5L);
        when(timeoutTimer.getElapsedSecs()).thenReturn(1L).thenReturn(1L).thenReturn(1L);

        Collection<StubHubEvent> returnedEvents = activeListingUpdateManager.update(accessToken, eventsToRetrieveListingsFor);

        verify(event1).addUpdatedListings(event1ListingCollection);
        verify(event2).addUpdatedListings(event2ListingCollection);
        verify(event3, never()).addUpdatedListings(event3ListingCollection);
        assertTrue(returnedEvents.containsAll(Arrays.asList(event1, event2, event3)));
        verify(threadSleeper).sleep(2000L);
        verify(requestTimer, times(1)).start();
        verify(requestTimer, times(1)).reset();
    }

    @Test
    public void shouldUpdateActiveListingsWithTimeout() {
        activeListingUpdateManager = new EventListingUpdateControllerImpl(activeListingRetriever,
                listingCollectionFactory, requestTimer, timeoutTimer, threadSleeper, dateTimeFormatter
                ,MAX_LISTINGS_PER_REQUEST, MAX_REQUESTS_IN_MINUTE, 10, null, null);

        when(event1.getEventID()).thenReturn(eventId1);
        when(event2.getEventID()).thenReturn(eventId2);
        when(event3.getEventID()).thenReturn(eventId3);
        when(activeListingRetriever.retrieve(accessToken, eventId1, ROW_START_1, MAX_LISTINGS_PER_REQUEST)).thenReturn(event1Listings1);
        when(activeListingRetriever.retrieve(accessToken,eventId1, ROW_START_2, MAX_LISTINGS_PER_REQUEST)).thenReturn(event1Listings2);
        when(activeListingRetriever.retrieve(accessToken, eventId2, ROW_START_1, MAX_LISTINGS_PER_REQUEST)).thenReturn(event2Listings1);
        when(listingCollectionFactory.create(allEvent1Listings, dateOfListingRetrievals)).thenReturn(event1ListingCollection);
        when(listingCollectionFactory.create(allEvent2Listings, dateOfListingRetrievals)).thenReturn(event2ListingCollection);
        when(requestTimer.getElapsedSecs()).thenReturn(30L).thenReturn(55L).thenReturn(59L).thenReturn(5L);
        when(timeoutTimer.getElapsedSecs()).thenReturn(1L).thenReturn(10L);

        Collection<StubHubEvent> returnedEvents = activeListingUpdateManager.update(accessToken, eventsToRetrieveListingsFor);

        verify(activeListingRetriever, never()).retrieve(accessToken, eventId3, ROW_START_1, MAX_LISTINGS_PER_REQUEST);
        verify(event1).addUpdatedListings(event1ListingCollection);
        verify(event2).addUpdatedListings(event2ListingCollection);
        verify(event3, never()).addUpdatedListings(event3ListingCollection);
        assertTrue(returnedEvents.containsAll(Arrays.asList(event1, event2)));
        verify(threadSleeper).sleep(2000L);
        verify(requestTimer, times(1)).start();
        verify(requestTimer, times(1)).reset();
    }
}