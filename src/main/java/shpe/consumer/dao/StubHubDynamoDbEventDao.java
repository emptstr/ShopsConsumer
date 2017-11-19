package shpe.consumer.dao;

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import lombok.RequiredArgsConstructor;
import shpe.consumer.model.StubHubEvent;
import shpe.consumer.model.StubHubListingCollection;

import java.util.Collection;

/**
 * Created by jordan on 9/4/17.
 */
@RequiredArgsConstructor
public class StubHubDynamoDbEventDao extends StubHubEventDao {

    private static final String EVENT_TABLE_NAME = "events";
    private static final String HASH_KEY_NAME = "eventId";
    private static final String LISTING_COLLECTIONS_ATTR_STRING = "allListings";
    private static final String EVENT_STATUS_ATTR_STRING = "eventStatus";
    private static final String EVENT_DATE_ATTR_STRING = "eventDateUTC";

    private final Table table;
    private final AttributeUpdateFactory attributeUpdateFactory;

    public StubHubDynamoDbEventDao(DynamoDB dynamoDB, AttributeUpdateFactory attributeUpdateFactory){
        this.table = dynamoDB.getTable(EVENT_TABLE_NAME);
        this.attributeUpdateFactory = attributeUpdateFactory;
    }

    @Override
    public StubHubEvent getEvent(StubHubEvent.StubHubEventID eventId) {
        return null;
    }

    @Override
    public void saveEvent(StubHubEvent event1) {
        PrimaryKey eventKey = new PrimaryKey(HASH_KEY_NAME, event1.getEventID().getEventID());
        AttributeUpdate updateForEventDate = getEventDateAttributeUpdate(event1);
        AttributeUpdate updateForEventStatus = getEventStatusAttributeUpdate(event1);
        AttributeUpdate updateForListingCollections = getListingCollectionAttributeUpdate(event1);
        updateEvent(eventKey, updateForEventDate, updateForEventStatus, updateForListingCollections);
    }

    private void updateEvent(PrimaryKey eventKey, AttributeUpdate updateForEventDate, AttributeUpdate updateForEventStatus, AttributeUpdate updateForListingCollections) {
        try {
            table.updateItem(eventKey, updateForEventDate, updateForEventStatus, updateForListingCollections);
        }catch (Exception e){
            throw new RuntimeException("Failed while saving event with primary key " + eventKey.toString(), e);
        }
    }

    private AttributeUpdate getListingCollectionAttributeUpdate(StubHubEvent event1) {
        Collection<StubHubListingCollection> listingCollections = event1.getAllEventListings();
        return attributeUpdateFactory.getInstance(LISTING_COLLECTIONS_ATTR_STRING)
                .addElements(listingCollections.toArray(new StubHubListingCollection[listingCollections.size()]));
    }

    private AttributeUpdate getEventStatusAttributeUpdate(StubHubEvent event1) {
        return attributeUpdateFactory.getInstance(EVENT_STATUS_ATTR_STRING)
                .put(event1.getEventStatus());
    }

    private AttributeUpdate getEventDateAttributeUpdate(StubHubEvent event1) {
        return attributeUpdateFactory.getInstance(EVENT_DATE_ATTR_STRING)
                .put(event1.getEventDateUTC());
    }
}
