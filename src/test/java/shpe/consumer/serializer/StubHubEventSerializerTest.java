package shpe.consumer.serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import shpe.consumer.model.StubHubEvent;
import shpe.consumer.model.StubHubListing;
import shpe.consumer.model.StubHubListingCollection;
import shpe.consumer.model.TicketDeliveryInfo;

import java.util.Arrays;

import static shpe.consumer.model.StubHubEvent.*;
import static shpe.consumer.model.StubHubEvent.StubHubAncestors.*;

/**
 * Created by jordan on 9/3/17.
 */
public class StubHubEventSerializerTest {

    private static final String EVENT_ID_1 = "eventId";
    private static final String VENUE_ID_1 = "venueId";
    private static final String CITY_1 = "city";
    private static final String STATE_1 = "state";
    private static final String POSTAL_CODE_1 = "zipcode";
    private static final String COUNTRY = "country";
    private static final String CATEGORY_ID_1 = "categoryId";
    private static final String GROUPING_ID_1 = "groupingId";
    private static final String PERFORMER_ID_1 = "performerId";
    private static final String LISTING_ID_1 = "listingId";

    StubHubEvent expectedEvent1 = new StubHubEvent(new StubHubEventID(EVENT_ID_1), EventStatus.Active,
            new LocalDateTime("2017-08-07T23:05:00"),
            new StubHubVenue(VENUE_ID_1, CITY_1, STATE_1, POSTAL_CODE_1, COUNTRY),
            new StubHubAncestors(Arrays.asList(new StubHubCategory(CATEGORY_ID_1)),
                    Arrays.asList(new StubHubGrouping(GROUPING_ID_1)), Arrays.asList(new StubHubPerformer(PERFORMER_ID_1))));

    StubHubListing listing1 = new StubHubListing(LISTING_ID_1, Money.of(CurrencyUnit.of("USD"), 35.00),
            Money.of(CurrencyUnit.of("USD"), 20.00), 2, TicketDeliveryInfo.UPS, false);

    StubHubListing listing2 = new StubHubListing(LISTING_ID_1, Money.of(CurrencyUnit.of("USD"), 25.00),
            Money.of(CurrencyUnit.of("USD"), 30.00), 1, TicketDeliveryInfo.UPS, false);

    StubHubListingCollection listingCollection = new StubHubListingCollection(DateTime.now(), Arrays.asList(listing1, listing2));

    @Test
    public void printJson() throws Exception {
        expectedEvent1.addUpdatedListings(listingCollection);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(StubHubEvent.class, new StubHubEventSerializer());
        Gson gson = gsonBuilder.create();
        String json = gson.toJson(expectedEvent1, StubHubEvent.class);
        System.out.println(json);
    }

}