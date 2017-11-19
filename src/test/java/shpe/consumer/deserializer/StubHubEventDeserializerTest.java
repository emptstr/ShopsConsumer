package shpe.consumer.deserializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import shpe.consumer.model.StubHubEvent;
import shpe.consumer.model.StubHubEvent.StubHubAncestors;
import shpe.consumer.model.StubHubEvent.StubHubVenue;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static shpe.consumer.model.StubHubEvent.EventStatus;
import static shpe.consumer.model.StubHubEvent.StubHubAncestors.*;
import static shpe.consumer.model.StubHubEvent.StubHubEventID;

/**
 * Created by jordan on 7/4/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class StubHubEventDeserializerTest {


    private static final String JSON = "{\n" +
            "    \"numFound\": 104139,\n" +
            "    \"events\": [\n" +
            "        {\n" +
            "            \"id\": 9735794,\n" +
            "            \"status\": \"Active\",\n" +
            "            \"locale\": \"en_US\",\n" +
            "            \"eventDateUTC\": \"2017-08-07T23:05:00+0000\",\n" +
            "            \"venue\": {\n" +
            "                \"id\": 35177,\n" +
            "                \"name\": \"Fifth Third Field  (Toledo)\",\n" +
            "                \"url\": \"fifth-third-field-toledo-event-tickets\",\n" +
            "                \"webURI\": \"fifth-third-field-toledo-event-tickets/venue/35177/\",\n" +
            "                \"seoURI\": \"fifth-third-field-toledo-event-tickets\",\n" +
            "                \"venueUrl\": \"fifth-third-field-toledo-event-tickets\",\n" +
            "                \"latitude\": 41.648557,\n" +
            "                \"longitude\": -83.538602,\n" +
            "                \"timezone\": \"EDT\",\n" +
            "                \"jdkTimezone\": \"US/Eastern\",\n" +
            "                \"address1\": \"406 Washington St.\",\n" +
            "                \"city\": \"Toledo\",\n" +
            "                \"state\": \"OH\",\n" +
            "                \"postalCode\": \"43604\",\n" +
            "                \"country\": \"US\",\n" +
            "                \"venueConfigId\": 39207\n" +
            "            },\n" +
            "            \"venueConfiguration\": {\n" +
            "                \"id\": 39207,\n" +
            "                \"name\": \"Baseball - Dynamic\"\n" +
            "            },\n" +
            "            \"ancestors\": {\n" +
            "                \"categories\": [\n" +
            "                    {\n" +
            "                        \"id\": 28,\n" +
            "                        \"name\": \"Sports\",\n" +
            "                        \"url\": \"sports-tickets\",\n" +
            "                        \"webURI\": \"sports-tickets/category/28/\",\n" +
            "                        \"seoURI\": \"sports-tickets\"\n" +
            "                    } \n" +
            "                ],\n" +
            "                \"groupings\": [\n" +
            "                    {\n" +
            "                        \"id\": 1143,\n" +
            "                        \"name\": \"Minor League Baseball Tickets - AAA\",\n" +
            "                        \"url\": \"minor-league-baseball-tickets-aaa\",\n" +
            "                        \"webURI\": \"minor-league-baseball-tickets-aaa/grouping/1143/\",\n" +
            "                        \"seoURI\": \"minor-league-baseball-tickets-aaa\"\n" +
            "                    }\n" +
            "                ],\n" +
            "                \"performers\": [\n" +
            "                    {\n" +
            "                        \"id\": 46996,\n" +
            "                        \"name\": \"Toledo Mud Hens\",\n" +
            "                        \"url\": \"toledo-mud-hens-tickets\",\n" +
            "                        \"webURI\": \"toledo-mud-hens-tickets/performer/46996/\",\n" +
            "                        \"seoURI\": \"toledo-mud-hens-tickets\"\n" +
            "                    }\n" +
            "                ],\n" +
            "                \"geos\": [\n" +
            "                    {\n" +
            "                        \"id\": 0\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"id\": 196976,\n" +
            "                        \"name\": \"United States\",\n" +
            "                        \"url\": \"united-states-tickets\",\n" +
            "                        \"webURI\": \"united-states-tickets/geography/196976/\",\n" +
            "                        \"seoURI\": \"united-states-tickets\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"id\": 686,\n" +
            "                        \"name\": \"Ohio\",\n" +
            "                        \"url\": \"ohio-tickets\",\n" +
            "                        \"webURI\": \"ohio-tickets/geography/686/\",\n" +
            "                        \"seoURI\": \"ohio-tickets\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"id\": 3261,\n" +
            "                        \"name\": \"Cleveland\",\n" +
            "                        \"url\": \"cleveland-tickets\",\n" +
            "                        \"webURI\": \"cleveland-tickets/geography/3261/\",\n" +
            "                        \"seoURI\": \"cleveland-tickets\"\n" +
            "                    }\n" +
            "                ]\n" +
            "            },\n" +
            "            \"sourceId\": \"1\",\n" +
            "            \"defaultLocale\": \"en_US\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": 9735802,\n" +
            "            \"status\": \"Active\",\n" +
            "            \"locale\": \"en_US\",\n" +
            "            \"eventDateUTC\": \"2017-08-13T22:05:00+0000\",\n" +
            "            \"venue\": {\n" +
            "                \"id\": 35177,\n" +
            "                \"name\": \"Fifth Third Field  (Toledo)\",\n" +
            "                \"url\": \"fifth-third-field-toledo-event-tickets\",\n" +
            "                \"webURI\": \"fifth-third-field-toledo-event-tickets/venue/35177/\",\n" +
            "                \"seoURI\": \"fifth-third-field-toledo-event-tickets\",\n" +
            "                \"venueUrl\": \"fifth-third-field-toledo-event-tickets\",\n" +
            "                \"latitude\": 41.648557,\n" +
            "                \"longitude\": -83.538602,\n" +
            "                \"timezone\": \"EDT\",\n" +
            "                \"jdkTimezone\": \"US/Eastern\",\n" +
            "                \"address1\": \"406 Washington St.\",\n" +
            "                \"city\": \"Toledo\",\n" +
            "                \"state\": \"OH\",\n" +
            "                \"postalCode\": \"43604\",\n" +
            "                \"country\": \"US\",\n" +
            "                \"venueConfigId\": 39207\n" +
            "            },\n" +
            "            \"venueConfiguration\": {\n" +
            "                \"id\": 39207,\n" +
            "                \"name\": \"Baseball - Dynamic\"\n" +
            "            },\n" +
            "            \"ancestors\": {\n" +
            "                \"categories\": [\n" +
            "                    {\n" +
            "                        \"id\": 29,\n" +
            "                        \"name\": \"Baseball\",\n" +
            "                        \"url\": \"baseball-tickets\",\n" +
            "                        \"webURI\": \"baseball-tickets/category/29/\",\n" +
            "                        \"seoURI\": \"baseball-tickets\"\n" +
            "                    }\n" +
            "                ],\n" +
            "                \"groupings\": [\n" +
            "                    {\n" +
            "                        \"id\": 1143,\n" +
            "                        \"name\": \"Minor League Baseball Tickets - AAA\",\n" +
            "                        \"url\": \"minor-league-baseball-tickets-aaa\",\n" +
            "                        \"webURI\": \"minor-league-baseball-tickets-aaa/grouping/1143/\",\n" +
            "                        \"seoURI\": \"minor-league-baseball-tickets-aaa\"\n" +
            "                    }\n" +
            "                ],\n" +
            "                \"performers\": [\n" +
            "                    {\n" +
            "                        \"id\": 46996,\n" +
            "                        \"name\": \"Toledo Mud Hens\",\n" +
            "                        \"url\": \"toledo-mud-hens-tickets\",\n" +
            "                        \"webURI\": \"toledo-mud-hens-tickets/performer/46996/\",\n" +
            "                        \"seoURI\": \"toledo-mud-hens-tickets\"\n" +
            "                    }\n" +
            "                ],\n" +
            "                \"geos\": [\n" +
            "                    {\n" +
            "                        \"id\": 0\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"id\": 196976,\n" +
            "                        \"name\": \"United States\",\n" +
            "                        \"url\": \"united-states-tickets\",\n" +
            "                        \"webURI\": \"united-states-tickets/geography/196976/\",\n" +
            "                        \"seoURI\": \"united-states-tickets\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"id\": 686,\n" +
            "                        \"name\": \"Ohio\",\n" +
            "                        \"url\": \"ohio-tickets\",\n" +
            "                        \"webURI\": \"ohio-tickets/geography/686/\",\n" +
            "                        \"seoURI\": \"ohio-tickets\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"id\": 3261,\n" +
            "                        \"name\": \"Cleveland\",\n" +
            "                        \"url\": \"cleveland-tickets\",\n" +
            "                        \"webURI\": \"cleveland-tickets/geography/3261/\",\n" +
            "                        \"seoURI\": \"cleveland-tickets\"\n" +
            "                    }\n" +
            "                ]\n" +
            "            },\n" +
            "            \"sourceId\": \"1\",\n" +
            "            \"defaultLocale\": \"en_US\"\n" +
            "        }\n" +
            "        ]\n" +
            "}" ;
    private static final String EVENT_ID_1 = "9735794";
    private static final String EVENT_ID_2 = "9735802";
    private static final String VENUE_ID_1 = "35177";
    private static final String VENUE_ID_2 = "35177";
    private static final String CITY_1 = "Toledo";
    private static final String CITY_2 = "Toledo";
    private static final String STATE_1 = "OH";
    private static final String STATE_2 = "OH";
    private static final String POSTAL_CODE_1 = "43604" ;
    private static final String POSTAL_CODE_2 = "43604";
    private static final String CATEGORY_ID_1 = "28";
    private static final String CATEGORY_ID_2 = "29";
    private static final String GROUPING_ID_1 = "1143";
    private static final String GROUPING_ID_2 = "1143";
    private static final String PERFORMER_ID_1 = "46996";
    private static final String PERFORMER_ID_2 = "46996";
    private static final String COUNTRY = "US";

    private StubHubEventDeserializer eventDeserializer;
    private GsonBuilder gsonBuilder;
    private Gson gson;

    private StubHubEvent expectedEvent1, expectedEvent2;
    private List<StubHubEvent> expectedResult;

    @Before
    public void setUp() throws Exception {
        expectedEvent1 = new StubHubEvent(new StubHubEventID(EVENT_ID_1), EventStatus.Active,
                new LocalDateTime("2017-08-07T23:05:00"),
                new StubHubVenue(VENUE_ID_1,CITY_1, STATE_1, POSTAL_CODE_1, COUNTRY),
                new StubHubAncestors(Arrays.asList(new StubHubCategory(CATEGORY_ID_1)),
                        Arrays.asList(new StubHubGrouping(GROUPING_ID_1)), Arrays.asList(new StubHubPerformer(PERFORMER_ID_1))));

        expectedEvent2 = new StubHubEvent(new StubHubEventID(EVENT_ID_2), EventStatus.Active,
                new LocalDateTime("2017-08-13T22:05:00"),
                new StubHubVenue(VENUE_ID_2,CITY_2, STATE_2, POSTAL_CODE_2, COUNTRY),
                new StubHubAncestors(Arrays.asList(new StubHubCategory(CATEGORY_ID_2)),
                        Arrays.asList(new StubHubGrouping(GROUPING_ID_2)), Arrays.asList(new StubHubPerformer(PERFORMER_ID_2))));

        expectedResult = Arrays.asList(expectedEvent1, expectedEvent2);

        eventDeserializer = new StubHubEventDeserializer();
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(StubHubEvent[].class, new StubHubEventDeserializer());
        gson = gsonBuilder.create();
    }

    @Test
    public void testDeserialize() {
        StubHubEvent[] actualResult = gson.fromJson(JSON, StubHubEvent[].class);

        for(int i =0; i < expectedResult.size(); i++){
            StubHubEvent actualEvent = actualResult[i];
            StubHubEvent expectedEvent = expectedResult.get(i);

            assertEquals(actualEvent.getEventID(), expectedEvent.getEventID());
            assertEquals(actualEvent.getEventStatus(), expectedEvent.getEventStatus());
            assertEquals(actualEvent.getEventDateUTC(), expectedEvent.getEventDateUTC());

            StubHubVenue actualVenue = actualEvent.getVenue();
            StubHubVenue expectedVenue = expectedEvent.getVenue();
            assertEquals(actualVenue.getVenueID(), expectedVenue.getVenueID());
            assertEquals(actualVenue.getCity(), expectedVenue.getCity());
            assertEquals(actualVenue.getCountry(), expectedVenue.getCountry());
            assertEquals(actualVenue.getPostalCode(), expectedVenue.getPostalCode());
            assertEquals(actualVenue.getState(), expectedVenue.getState());

            StubHubAncestors actualAncestors = actualEvent.getAncestor();
            StubHubAncestors expectedAncestors = expectedEvent.getAncestor();

            StubHubCategory actualCategory = actualAncestors.getCategories().get(0);
            StubHubCategory expectedCategory = expectedAncestors.getCategories().get(0);
            assertEquals(actualCategory.getCategoryID(), expectedCategory.getCategoryID());

            StubHubGrouping actualGrouping = actualAncestors.getGroupings().get(0);
            StubHubGrouping expectedGrouping = expectedAncestors.getGroupings().get(0);
            assertEquals(actualGrouping.getGroupingID(), expectedGrouping.getGroupingID());

            StubHubPerformer actualPerformer = actualAncestors.getPerformers().get(0);
            StubHubPerformer expectedPerformer = expectedAncestors.getPerformers().get(0);
            assertEquals(actualPerformer.getPerformerID(), expectedPerformer.getPerformerID());
        }
    }

}