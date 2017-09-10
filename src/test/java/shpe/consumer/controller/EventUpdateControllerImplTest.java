package shpe.consumer.controller;

import com.codahale.metrics.MetricRegistry;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.verification.Times;
import org.mockito.runners.MockitoJUnitRunner;
import shpe.consumer.accessor.EventApiAccessorImpl;
import shpe.consumer.model.*;
import shpe.util.Sleeper;
import shpe.util.Timer;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static shpe.consumer.model.StubHubEvent.*;

/**
 * Created by jordan on 6/22/17.x
 */
@RunWith(MockitoJUnitRunner.class)
public class EventUpdateControllerImplTest {

    private static final int MAX_EVENTS_PER_REQUEST = 2;
    private static final int MAX_REQUESTS_PER_MINUTE = 1;
    private static final int ROW_START_1 = 0;
    private static final int ROW_START_2 = 2;
    private static final String ACCESS_TOKEN_STRING = "ACCESS_TOKEN";
    private static final LocalDateTime SECONDS_T0_EXP = LocalDateTime.now();
    private static final String EVENT_ID_1 = "EVENT_ID_1";
    private static final String EVENT_ID_2 = "EVENT_ID_2";
    private static final String EVENT_ID_3 = "EVENT_ID_3";

    private EventUpdateController activeEventUpdateManager;

    @Mock
    private EventApiAccessorImpl eventRetriever;
    @Mock
    private Timer requestTimer, timeoutTimer;
    @Mock
    private Sleeper threadSleeper;
    @Mock
    StubHubVenue venue1, venue2, venue3;
    @Mock
    StubHubAncestors ancestor1, ancestor2, ancestor3;

    MetricRegistry metricRegistry = new MetricRegistry();

    private StubHubApiToken accessToken;
    private List<StubHubEvent> eventList_1, eventList_2;
    private StubHubEvent event_1, event_2, event_3;
    private StubHubEventID event_id_1, event_id_2, event_id_3;

    @Before
    public void setup() {
        accessToken = new StubHubApiToken(ACCESS_TOKEN_STRING, SECONDS_T0_EXP);
        event_id_1 = new StubHubEventID(EVENT_ID_1);
        event_id_2 = new StubHubEventID(EVENT_ID_2);
        event_id_3 = new StubHubEventID(EVENT_ID_3);
        event_1 = new StubHubEvent(event_id_1, EventStatus.Active, LocalDateTime.now(), venue1, ancestor1);
        event_2 = new StubHubEvent(event_id_2, EventStatus.Active, LocalDateTime.now(), venue2, ancestor2);
        event_3 = new StubHubEvent(event_id_3, EventStatus.Active, LocalDateTime.now(), venue3, ancestor3);
        eventList_1 = Arrays.asList(event_1, event_2);
        eventList_2 = Arrays.asList(event_3);

        activeEventUpdateManager = new EventUpdateControllerImpl(eventRetriever, timeoutTimer,
                requestTimer, threadSleeper, MAX_EVENTS_PER_REQUEST, MAX_REQUESTS_PER_MINUTE, 10, metricRegistry.histogram("test"),
                metricRegistry.histogram("test"));
    }

    @Test
    public void shouldUpdate() throws Exception {
        when(eventRetriever.getEvents(accessToken, ROW_START_1, MAX_EVENTS_PER_REQUEST)).thenReturn(eventList_1);
        when(requestTimer.getElapsedSecs()).thenReturn(10L).thenReturn(10L).thenReturn(45L).thenReturn(45L);
        when(timeoutTimer.getElapsedSecs()).thenReturn(1L).thenReturn(1L);
        when(eventRetriever.getEvents(accessToken, ROW_START_2, MAX_EVENTS_PER_REQUEST)).thenReturn(eventList_2);

        Collection<StubHubEvent> updatedEvents = activeEventUpdateManager.update(accessToken);

        verify(threadSleeper).sleep(16000);
        verify(requestTimer).start();
        verify(requestTimer).reset();
        verify(timeoutTimer).start();

        Collection<StubHubEvent> expectedIds = Arrays.asList(event_1, event_2, event_3);

        assertEquals(3, updatedEvents.size());
        assertTrue(updatedEvents.containsAll(expectedIds));
    }

    @Test
    public void shouldUpdateWithTimeout() throws Exception {
        when(eventRetriever.getEvents(accessToken, ROW_START_1, MAX_EVENTS_PER_REQUEST)).thenReturn(eventList_1);
        when(requestTimer.getElapsedSecs()).thenReturn(10L).thenReturn(45L);
        when(timeoutTimer.getElapsedSecs()).thenReturn(10L);

        Collection<StubHubEvent> eventsUpdatedBeforeTimeout = activeEventUpdateManager.update(accessToken);

        verify(requestTimer).start();
        verify(timeoutTimer).start();
        verify(threadSleeper, never()).sleep(16000);
        verify(requestTimer, never()).reset();

        assertEquals(2, eventsUpdatedBeforeTimeout.size());
        assertTrue(eventsUpdatedBeforeTimeout.containsAll(Arrays.asList(event_1, event_2)));
    }

    @Test
    public void shouldUpdateAfterFailure() throws Exception {
        when(eventRetriever.getEvents(accessToken, ROW_START_1, MAX_EVENTS_PER_REQUEST)).thenReturn(eventList_1);
        when(requestTimer.getElapsedSecs()).thenReturn(10L).thenReturn(10L).thenReturn(45L).thenReturn(45L);
        when(timeoutTimer.getElapsedSecs()).thenReturn(1L).thenReturn(1L);
        when(eventRetriever.getEvents(accessToken, ROW_START_2, MAX_EVENTS_PER_REQUEST)).thenThrow(new RuntimeException());

        Collection<StubHubEvent> updatedEvents = activeEventUpdateManager.update(accessToken);

        verify(threadSleeper, times(2)).sleep(16000);
        verify(requestTimer).start();
        verify(requestTimer, times(2)).reset();
        verify(timeoutTimer).start();

        Collection<StubHubEvent> expectedIds = Arrays.asList(event_1, event_2);

        assertEquals(2, updatedEvents.size());
        assertTrue(updatedEvents.containsAll(expectedIds));
    }
}