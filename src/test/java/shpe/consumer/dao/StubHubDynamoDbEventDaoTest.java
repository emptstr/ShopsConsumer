package shpe.consumer.dao;

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import shpe.consumer.model.StubHubEvent;
import shpe.consumer.model.StubHubListing;
import shpe.consumer.model.StubHubListingCollection;
import shpe.consumer.model.TicketDeliveryInfo;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class StubHubDynamoDbEventDaoTest {

    private static final String EVENT_TABLE_NAME = "events";
    private StubHubDynamoDbEventDao dynamoEventDao;
    @Mock
    private DynamoDB dyanmoDb;
    @Mock
    private Table eventTable;
    @Mock
    private AttributeUpdateFactory attributeUpdateFactory;
    @Mock
    private AttributeUpdate eventDateAttributeUpdate, eventStatusAttributeUpdate,
            listingCollectionAttributeUpdate, eventDateAttributeUpdate2, eventStatusAttributeUpdate2, listingCollectionAttributeUpdate2;
    private StubHubEvent eventToBeSaved;
    private PrimaryKey primaryKey;


    @Before
    public void setup() throws Exception {
         eventToBeSaved = new StubHubEvent(new StubHubEvent.StubHubEventID("eventId"), StubHubEvent.EventStatus.Active,
                new LocalDateTime("2017-08-07T23:05:00"),
                new StubHubEvent.StubHubVenue("venue", "city", "state", "zip", "country"),
                new StubHubEvent.StubHubAncestors(Arrays.asList(new StubHubEvent.StubHubAncestors.StubHubCategory("category")),
                        Arrays.asList(new StubHubEvent.StubHubAncestors.StubHubGrouping("groupingId")),
                        Arrays.asList(new StubHubEvent.StubHubAncestors.StubHubPerformer("performer"))));

        StubHubListing listing1 = new StubHubListing("listing1", Money.of(CurrencyUnit.of("USD"), 35.00),
                Money.of(CurrencyUnit.of("USD"), 20.00), 2, TicketDeliveryInfo.UPS, false);

        StubHubListing listing2 = new StubHubListing("listing2", Money.of(CurrencyUnit.of("USD"), 25.00),
                Money.of(CurrencyUnit.of("USD"), 30.00), 1, TicketDeliveryInfo.UPS, false);

        StubHubListingCollection listingCollection = new StubHubListingCollection(DateTime.now(), Arrays.asList(listing1, listing2));

        eventToBeSaved.addUpdatedListings(listingCollection);

        primaryKey = new PrimaryKey("eventId", eventToBeSaved.getEventID().getEventID());
    }


    @Test
    public void shouldSaveToDynamo() throws Exception {
        when(dyanmoDb.getTable(EVENT_TABLE_NAME)).thenReturn(eventTable);
        dynamoEventDao = new StubHubDynamoDbEventDao(dyanmoDb, attributeUpdateFactory);
        when(attributeUpdateFactory.getInstance("eventDateUTC")).thenReturn(eventDateAttributeUpdate);
        when(attributeUpdateFactory.getInstance("eventStatus")).thenReturn(eventStatusAttributeUpdate);
        when(attributeUpdateFactory.getInstance("allListings")).thenReturn(listingCollectionAttributeUpdate);
        when(eventDateAttributeUpdate.put(eventToBeSaved.getEventDateUTC())).thenReturn(eventDateAttributeUpdate2);
        when(eventStatusAttributeUpdate.put(eventToBeSaved.getEventStatus())).thenReturn(eventStatusAttributeUpdate2);
        Collection<StubHubListingCollection> listingCollections =  eventToBeSaved.getAllEventListings();
        StubHubListingCollection[] listingCollectionsArray = listingCollections.
                toArray(new StubHubListingCollection[listingCollections.size()]);
        when(listingCollectionAttributeUpdate.addElements(listingCollectionsArray)).thenReturn(listingCollectionAttributeUpdate2);

        dynamoEventDao.saveEvent(eventToBeSaved);

        verify(eventTable).updateItem(primaryKey, eventDateAttributeUpdate2,
                eventStatusAttributeUpdate2, listingCollectionAttributeUpdate2);
    }
}