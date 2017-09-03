package shpe.consumer.deserializer;

import com.google.gson.*;
import org.joda.time.LocalDateTime;
import shpe.consumer.model.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static shpe.consumer.model.StubHubEvent.*;
import static shpe.consumer.model.StubHubEvent.StubHubAncestors.*;

/**
 * @author Jordan Gaston
 * @version 0.1.17
 */
public class StubHubEventDeserializer implements JsonDeserializer<StubHubEvent[]> {

    private static final String EVENTS = "events";
    private static final String ID = "id";
    private static final String STATUS = "status";
    private static final String EVENT_DATE = "eventDateUTC";
    private static final String VENUE = "venue";
    private static final String ANCESTORS = "ancestors";
    private static final String CITY = "city";
    private static final String STATE = "state";
    private static final String POSTAL_CODE = "postalCode";
    private static final String COUNTRY = "country";
    private static final String CATEGORIES = "categories";
    private static final String GROUPINGS = "groupings";
    private static final String PERFORMERS = "performers";

    @Override
    public StubHubEvent[] deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonObject responseObject = jsonElement.getAsJsonObject();// retrieving the root JSON response object
        final JsonArray eventJsonList = responseObject.get(EVENTS).getAsJsonArray();// retrieving the array of JSON events

        StubHubEvent[] eventList = new StubHubEvent[eventJsonList.size()];

        for (int i = 0; i < eventList.length; i++) {
            final JsonObject jsonEvent = eventJsonList.get(i).getAsJsonObject();
            eventList[i] = (jsonToEvent(jsonEvent));
        }

        return eventList;
    }

    private StubHubEvent jsonToEvent(final JsonObject jsonEvent) {
        StubHubEventID eventID = new StubHubEventID(jsonEvent.get(ID).getAsString());
        EventStatus eventStatus = EventStatus.valueOf(jsonEvent.get(STATUS).getAsString());
        LocalDateTime eventDateUTC = new LocalDateTime(jsonEvent.get(EVENT_DATE).getAsString().split("\\+")[0]); // trimming the access off the original date format (ex. 2017-08-07T23:05:00+0000 -> 2017-08-07T23:05:00)
        StubHubVenue venue = jsonToVenue(jsonEvent.get(VENUE).getAsJsonObject());
        StubHubAncestors ancestors = jsonToAncestors(jsonEvent.get(ANCESTORS).getAsJsonObject());
        return new StubHubEvent(eventID, eventStatus, eventDateUTC, venue, ancestors);
    }

    private StubHubVenue jsonToVenue(final JsonObject jsonVenue) {
        String venueID = jsonVenue.get(ID).getAsString();
        String city = jsonVenue.get(CITY).getAsString();
        String state = jsonVenue.get(STATE).getAsString();
        String postalCode = jsonVenue.get(POSTAL_CODE).getAsString();
        String country = jsonVenue.get(COUNTRY).getAsString();
        return new StubHubVenue(venueID, city, state, postalCode, country);
    }

    private StubHubAncestors jsonToAncestors(JsonObject jsonAncestors) {
        final JsonArray jsonCategories = jsonAncestors.get(CATEGORIES).getAsJsonArray();
        final JsonArray jsonGroupings = jsonAncestors.get(GROUPINGS).getAsJsonArray();
        final JsonArray jsonPerformers = jsonAncestors.get(PERFORMERS).getAsJsonArray();

        List<StubHubEvent.StubHubAncestors.StubHubCategory> categories = new ArrayList<>(jsonCategories.size());
        List<StubHubEvent.StubHubAncestors.StubHubGrouping> groupings = new ArrayList<>(jsonGroupings.size());
        List<StubHubEvent.StubHubAncestors.StubHubPerformer> performers = new ArrayList<>(jsonPerformers.size());

        for (int i = 0; i < jsonCategories.size(); i++) {
            final JsonObject jsonCategory = jsonCategories.get(i).getAsJsonObject();
            categories.add(jsonToCategory(jsonCategory));
        }

        for (int i = 0; i < jsonGroupings.size(); i++) {
            final JsonObject jsonGrouping = jsonGroupings.get(i).getAsJsonObject();
            groupings.add(jsonToGrouping(jsonGrouping));
        }

        for (int i = 0; i < jsonPerformers.size(); i++) {
            final JsonObject jsonPerformer = jsonPerformers.get(i).getAsJsonObject();
            performers.add(jsonToPerformer(jsonPerformer));
        }
        return new StubHubAncestors(categories, groupings, performers);
    }

    private StubHubCategory jsonToCategory(JsonObject jsonCategory) {
        return new StubHubCategory(jsonCategory.get(ID).getAsString());
    }

    private StubHubGrouping jsonToGrouping(JsonObject jsonGrouping) {
        return new StubHubGrouping(jsonGrouping.get(ID).getAsString());
    }

    private StubHubPerformer jsonToPerformer(JsonObject jsonPerformer) {
        return new StubHubPerformer(jsonPerformer.get(ID).getAsString());
    }
}
