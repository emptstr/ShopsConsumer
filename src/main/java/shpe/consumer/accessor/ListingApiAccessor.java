package shpe.consumer.accessor;

import shpe.consumer.model.StubHubApiToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import shpe.consumer.deserializer.StubHubListingDeserializer;
import shpe.consumer.model.StubHubEvent;
import shpe.consumer.model.StubHubListing;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@RequiredArgsConstructor
public class ListingApiAccessor {

    private static final String EVENT_TARGET_FORMAT_STRING = "https://api.stubhub.com/search/inventory/v2?eventId=%s" +
            "&versionNumber=v2&pricingsummary=true&start=%d&rows=%d";
    private static final String AUTH_HEADER_FORMAT_STRING = "Bearer %s";
    private final Client eventApiClient;
    private final GsonBuilder gsonBuilder;
    private final StubHubListingDeserializer listingDeserializer;

    public Collection<StubHubListing> retrieve(StubHubApiToken accessToken, StubHubEvent.StubHubEventID eventId, int rowStart1, int maxListingsPerRequest){
        String eventRequestTarget = String.format(EVENT_TARGET_FORMAT_STRING, eventId.getEventID(), rowStart1, maxListingsPerRequest);
        WebTarget numEventsTarget = eventApiClient.target(eventRequestTarget);
        Invocation.Builder eventRequestBuilder = numEventsTarget.request();
        eventRequestBuilder.header(HttpHeaders.ACCEPT_ENCODING, MediaType.APPLICATION_JSON);
        eventRequestBuilder.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
        eventRequestBuilder.header(HttpHeaders.AUTHORIZATION, String.format(AUTH_HEADER_FORMAT_STRING, accessToken.getTokenString()));
        Invocation numFoundInvocation = eventRequestBuilder.buildGet();
        String jsonResponse = numFoundInvocation.invoke(String.class);
        gsonBuilder.registerTypeAdapter(StubHubListing[].class, listingDeserializer);
        Gson gson = gsonBuilder.create();
        return new ArrayList<>(Arrays.asList(gson.fromJson(jsonResponse, StubHubListing[].class)));
    }
}
