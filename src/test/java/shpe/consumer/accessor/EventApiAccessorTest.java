package shpe.consumer.accessor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import shpe.consumer.deserializer.StubHubEventDeserializer;
import shpe.consumer.model.StubHubApiToken;
import shpe.consumer.model.StubHubEvent;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static shpe.consumer.model.StubHubEvent.*;

/**
 * Created by jordan on 7/1/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Gson.class, GsonBuilder.class})
public class EventApiAccessorTest {

    private static final String EVENT_TARGET_FORMAT_STRING = "https://api.stubhub.com/search/catalog/events/v3?fieldList=id,status,eventDateUTC,venue,ancestors&start=%d&rows=%d";
    private static final String NUM_EVENT_TARGET = "https://api.stubhub.com/search/catalog/events/v3?fieldList=numFound";
    private static final Integer NUM_EVENTS_FOUND = 0;
    private static final String AUTH_HEADER_FORMAT_STRING = "Bearer %s";
    private static final LocalDateTime SECONDS_TO_EXPR = LocalDateTime.now();
    private static final String ACCESS_TOKEN_STRING = "ACCESS_TOKEN_STRING";
    private static final java.lang.String EVENT_JSON_STRING = "JSON_STRING";
    private static final int ROW_START_1 = 1;
    private static final int NUM_ROWS = 1;

    private EventApiAccessorImpl activeEventUpdateManager;

    @Mock
    private WebTarget eventApiTarget;
    @Mock
    private Client client;
    @Mock
    private javax.ws.rs.client.Invocation.Builder eventApiRequestBuilder;
    @Mock
    private Invocation numFoundInvocation, eventApiInvocation;
    @Mock
    private StubHubVenue venue_1, venue_2;
    @Mock
    private StubHubAncestors ancestors_1, ancestors_2;
    @Mock
    private StubHubEventDeserializer eventArrayDeserializer;
    private GsonBuilder gsonBuilder = PowerMockito.mock(GsonBuilder.class);
    private Gson gson = PowerMockito.mock(Gson.class);

    private StubHubApiToken accessToken;
    private StubHubEvent event_1, event_2;
    private Collection<StubHubEvent> eventList;
    private StubHubEvent[] eventArray = new StubHubEvent[2];



    @Before
    public void setUp() throws Exception {
        event_1 = new StubHubEvent(new StubHubEventID("event_1"), EventStatus.Active, LocalDateTime.now(), venue_1, ancestors_1);
        event_2 = new StubHubEvent(new StubHubEventID("event_2"), EventStatus.Active, LocalDateTime.now(), venue_2, ancestors_2);
        eventArray[0] = event_1;
        eventArray[1] = event_2;
        eventList = Arrays.asList(event_1, event_2);
        accessToken = new StubHubApiToken(ACCESS_TOKEN_STRING, SECONDS_TO_EXPR);
        activeEventUpdateManager = new EventApiAccessorImpl(client,gsonBuilder, eventArrayDeserializer);
    }

    @Test
    public void testGetEvents() throws Exception {
        String eventRequestString1 = String.format(EVENT_TARGET_FORMAT_STRING, ROW_START_1, NUM_ROWS);
        when(client.target(eventRequestString1)).thenReturn(eventApiTarget);
        when(eventApiTarget.request()).thenReturn(eventApiRequestBuilder);
        when(eventApiRequestBuilder.buildGet()).thenReturn(eventApiInvocation);
        when(eventApiInvocation.invoke(String.class)).thenReturn(EVENT_JSON_STRING);
        when(gsonBuilder.create()).thenReturn(gson);
        when(gson.fromJson(EVENT_JSON_STRING,StubHubEvent[].class)).thenReturn(eventArray);

        Collection<StubHubEvent> actualResult = activeEventUpdateManager.getEvents(accessToken, ROW_START_1, NUM_ROWS);

        verify(gsonBuilder).registerTypeAdapter(StubHubEvent[].class, eventArrayDeserializer);
        verify(eventApiRequestBuilder).header(HttpHeaders.ACCEPT_ENCODING, MediaType.APPLICATION_JSON);
        verify(eventApiRequestBuilder).header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
        verify(eventApiRequestBuilder).header(HttpHeaders.AUTHORIZATION, String.format(AUTH_HEADER_FORMAT_STRING, accessToken.getTokenString()));

        for (StubHubEvent event : eventList) {
            if (!actualResult.contains(event)) {
                fail("The returned result did not contain the event with id " + event.getEventID().getEventID());
            }
        }
    }

}