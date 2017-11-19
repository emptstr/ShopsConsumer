package shpe.consumer.accessor;

import shpe.consumer.accessor.ListingApiAccessor;
import shpe.consumer.model.StubHubApiToken;
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
import shpe.consumer.deserializer.StubHubListingDeserializer;
import shpe.consumer.model.StubHubListing;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static shpe.consumer.model.StubHubEvent.*;

/**
 * Created by jordan on 7/1/17.
 */

@PrepareForTest({Gson.class, GsonBuilder.class})
@RunWith(PowerMockRunner.class)
public class ListingApiAccessorTest {

    private static final String LISTING_TARGET_FORMAT_STRING = "https://api.stubhub.com/search/inventory/v2?eventId=%s" +
            "&versionNumber=v2&pricingsummary=true&start=%d&rows=%d";
    private static final String AUTH_HEADER_FORMAT_STRING = "Bearer %s";
    private static final String ACCESS_TOKEN_STRING = "ACCESS_TOKEN_STRING";
    private static final java.lang.String LISTING_JSON_STRING = "JSON_STRING";
    private static final int ROW_START_1 = 0;
    private static final int NUM_ROWS = 1;

    private ListingApiAccessor activeEventUpdateManager;

    @Mock
    private WebTarget eventApiTarget;
    @Mock
    private Client client;
    @Mock
    private javax.ws.rs.client.Invocation.Builder eventApiRequestBuilder;
    @Mock
    private Invocation eventApiInvocation;
    @Mock
    private StubHubListingDeserializer eventArrayDeserializer;
    private GsonBuilder gsonBuilder = PowerMockito.mock(GsonBuilder.class);
    private Gson gson = PowerMockito.mock(Gson.class);

    @Mock
    private StubHubListing listing1, listing2;
    private StubHubEventID eventId1 = new StubHubEventID("eventID");
    private StubHubApiToken accessToken;

    private Collection<StubHubListing> eventList;
    private StubHubListing[] eventArray = new StubHubListing[2];


    @Before
    public void setUp() throws Exception {
        eventArray[0] = listing1;
        eventArray[1] = listing2;
        eventList = Arrays.asList(listing1, listing2);
        accessToken = new StubHubApiToken(ACCESS_TOKEN_STRING, LocalDateTime.now());
    }

    @Test
    public void testGetEvents() throws Exception {
        activeEventUpdateManager = new ListingApiAccessor(client, gsonBuilder, eventArrayDeserializer);

        String eventRequestString1 = String.format(LISTING_TARGET_FORMAT_STRING, eventId1.getEventID(), ROW_START_1, NUM_ROWS);
        when(client.target(eventRequestString1)).thenReturn(eventApiTarget);
        when(eventApiTarget.request()).thenReturn(eventApiRequestBuilder);
        when(eventApiRequestBuilder.buildGet()).thenReturn(eventApiInvocation);
        when(eventApiInvocation.invoke(String.class)).thenReturn(LISTING_JSON_STRING);
        when(gsonBuilder.create()).thenReturn(gson);
        when(gson.fromJson(LISTING_JSON_STRING, StubHubListing[].class)).thenReturn(eventArray);

        Collection<StubHubListing> actualResult = activeEventUpdateManager.retrieve(accessToken,eventId1, ROW_START_1, NUM_ROWS);

        verify(gsonBuilder).registerTypeAdapter(StubHubListing[].class, eventArrayDeserializer);
        verify(eventApiRequestBuilder).header(HttpHeaders.ACCEPT_ENCODING, MediaType.APPLICATION_JSON);
        verify(eventApiRequestBuilder).header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
        verify(eventApiRequestBuilder).header(HttpHeaders.AUTHORIZATION, String.format(AUTH_HEADER_FORMAT_STRING, accessToken.getTokenString()));

        for (StubHubListing event : eventList) {
            if (!actualResult.contains(event)) {
                fail("The returned result did not contain the event with id " + event.getListingId());
            }
        }
    }
}