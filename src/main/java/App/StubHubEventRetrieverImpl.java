package App;

import EventManagement.StubHubEvent;
import TokenManagement.StubHubApiToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jordan on 6/20/17.
 */
public class StubHubEventRetrieverImpl extends StubHubEventRetriever {
    private static final String NUM_EVENT_TARGET = "https://api.stubhub.com/search/catalog/events/v3?fieldList=numFound";
    private static final String EVENT_TARGET_FORMAT_STRING = "https://api.stubhub.com/search/catalog/events/v3?fieldList=id,status,eventDateUTC,venue,ancestors&start=%d&rows=%d";
    private static final String AUTH_HEADER_FORMAT_STRING = "Bearer %s";
    private final Client eventApiClient;
    private GsonBuilder gsonBuilder;
    private StubHubEventDeserializer eventArrayDeserializer;

    public StubHubEventRetrieverImpl(final Client eventApiClient, GsonBuilder gsonBuilder, StubHubEventDeserializer eventArrayDeserializer) {

        this.eventApiClient = eventApiClient;
        this.gsonBuilder = gsonBuilder;
        this.eventArrayDeserializer = eventArrayDeserializer;
    }

    public Integer getNumEvents(StubHubApiToken accessToken) {
        WebTarget numEventsTarget = eventApiClient.target(NUM_EVENT_TARGET);
        Invocation.Builder numFoundBuilder = numEventsTarget.request();
        numFoundBuilder.header(HttpHeaders.ACCEPT_ENCODING, MediaType.APPLICATION_JSON);
        numFoundBuilder.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
        numFoundBuilder.header(HttpHeaders.AUTHORIZATION, String.format(AUTH_HEADER_FORMAT_STRING, accessToken.getTokenString()));
        Invocation numFoundInvocation = numFoundBuilder.buildGet();
        return numFoundInvocation.invoke(Integer.class);
    }

    public List<StubHubEvent> getEvents(StubHubApiToken accessToken, int rowStart1, int eventsPerRequest) {
        String eventRequestTarget = String.format(EVENT_TARGET_FORMAT_STRING, rowStart1, eventsPerRequest);
        WebTarget numEventsTarget = eventApiClient.target(eventRequestTarget);
        Invocation.Builder eventRequestBuilder = numEventsTarget.request();
        eventRequestBuilder.header(HttpHeaders.ACCEPT_ENCODING, MediaType.APPLICATION_JSON);
        eventRequestBuilder.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
        eventRequestBuilder.header(HttpHeaders.AUTHORIZATION, String.format(AUTH_HEADER_FORMAT_STRING, accessToken.getTokenString()));
        Invocation numFoundInvocation = eventRequestBuilder.buildGet();
        String jsonResponse = numFoundInvocation.invoke(String.class);
        gsonBuilder.registerTypeAdapter(StubHubEvent[].class, eventArrayDeserializer);
        Gson gson = gsonBuilder.create();
        return new ArrayList<StubHubEvent>(Arrays.asList(gson.fromJson(jsonResponse, StubHubEvent[].class)));
    }
}
