package shpe.consumer.controller;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import shpe.consumer.controller.EventUpdateControllerImpl;
import shpe.consumer.accessor.EventApiAccessorImpl;
import shpe.consumer.controller.EventUpdateController;
import shpe.consumer.model.*;
import shpe.util.IndexList;
import shpe.util.IndexListFactory;
import shpe.util.Sleeper;
import shpe.util.Timer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static shpe.consumer.model.StubHubEvent.*;

/**
 * Created by jordan on 6/22/17.x
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class EventUpdateControllerImplTest {

    private static final int NUM_EVENTS = 4;
    private static final int MAX_EVENTS_PER_REQUEST = 2;
    private static final int MAX_REQUESTS_PER_MINUTE = 1;
    private static final int ROW_START_1 = 1;
    private static final int ROW_START_2 = 3;
    private static final String ACCESS_TOKEN_STRING = "ACCESS_TOKEN";
    private static final LocalDateTime SECONDS_T0_EXP = LocalDateTime.now();
    private static final String EVENT_ID_1 = "EVENT_ID_1";
    private static final String EVENT_ID_2 = "EVENT_ID_2";
    private static final String EVENT_ID_3 = "EVENT_ID_3";
    private static final String EVENT_ID_4 = "EVENT_ID_4";

    private EventUpdateController activeEventUpdateManager;

    @Mock
    private EventApiAccessorImpl eventRetriever;
    @Mock
    private Timer timer;
    @Mock
    private IndexListFactory indexListFactory;
    @Mock
    private IndexList indexList;
    @Mock
    private Iterator<Integer> indexListIterator;
    @Mock
    private Sleeper threadSleeper;
    @Mock
    StubHubVenue venue1, venue2, venue3, venue4;
    @Mock
    StubHubAncestors ancestor1, ancestor2, ancestor3, ancestor4;

    private StubHubApiToken accessToken;
    private List<StubHubEvent> eventList_1, eventList_2;
    private StubHubEvent event_1, event_2, event_3, event_4;
    private StubHubEventID event_id_1, event_id_2, event_id_3, event_id_4;



    @Before
    public void setup(){
        accessToken = new StubHubApiToken(ACCESS_TOKEN_STRING, SECONDS_T0_EXP);
        event_id_1 = new StubHubEventID(EVENT_ID_1);
        event_id_2 = new StubHubEventID(EVENT_ID_2);
        event_id_3 = new StubHubEventID(EVENT_ID_3);
        event_id_4 = new StubHubEventID(EVENT_ID_4);
        event_1 = new StubHubEvent(event_id_1, EventStatus.Active, LocalDateTime.now(), venue1, ancestor1);
        event_2 = new StubHubEvent(event_id_2, EventStatus.Active, LocalDateTime.now(), venue2, ancestor2);
        event_3 = new StubHubEvent(event_id_3, EventStatus.Active, LocalDateTime.now(), venue3, ancestor3);
        event_4 = new StubHubEvent(event_id_4, EventStatus.Active, LocalDateTime.now(), venue4, ancestor4);
        eventList_1 = Arrays.asList(event_1, event_2);
        eventList_2 = Arrays.asList(event_3, event_4);

        activeEventUpdateManager = new EventUpdateControllerImpl(eventRetriever, timer, threadSleeper, indexListFactory);
    }

    @Test
    public void testUpdate() throws Exception {
        when(eventRetriever.getNumEvents(accessToken)).thenReturn(NUM_EVENTS);

        when(indexListFactory.getInstance(NUM_EVENTS, MAX_EVENTS_PER_REQUEST, false)).thenReturn(indexList);
        when(indexList.iterator()).thenReturn(indexListIterator);

        when(eventRetriever.getEvents(accessToken, ROW_START_1, MAX_EVENTS_PER_REQUEST)).thenReturn(eventList_1);
        when(timer.getElapsedSecs()).thenReturn((long)10).thenReturn((long)45);
        when(eventRetriever.getEvents(accessToken, ROW_START_2, MAX_EVENTS_PER_REQUEST)).thenReturn(eventList_2);
        when(indexListIterator.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(indexListIterator.next()).thenReturn(ROW_START_1).thenReturn(ROW_START_2);

        Collection<StubHubEvent> updatedEvents = activeEventUpdateManager.update(accessToken);

        verify(threadSleeper).sleep(16000);
        verify(timer).start();
        verify(timer).reset();

        Collection<StubHubEvent> expectedIds = Arrays.asList(event_1, event_2, event_3, event_4);

        assertEquals(4, updatedEvents.size());
        assertTrue(updatedEvents.containsAll(expectedIds));
    }
}