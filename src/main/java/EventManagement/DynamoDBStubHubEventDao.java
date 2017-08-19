package EventManagement;

/**
 * Created by jordan on 7/11/17.
 * @see https://aws.amazon.com/blogs/developer/introducing-dynamodb-document-api-part-1/
 */
public class DynamoDBStubHubEventDao extends StubHubEventDao {
    @Override
    public StubHubEvent getEvent(StubHubEventID eventId) {
        return null;
    }

    @Override
    public void saveEvent(StubHubEvent event1) {

    }

    @Override
    public void updateEvent(final StubHubEvent updatedEvent){

    }
}
