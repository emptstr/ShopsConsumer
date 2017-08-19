package EventManagement;

/**
 * Created by jordan on 7/2/17.
 *
 */
public abstract class StubHubEventDao {

    public abstract StubHubEvent getEvent(final StubHubEventID eventId);

    public abstract void saveEvent(StubHubEvent event1);

    public abstract void updateEvent(StubHubEvent updatedEvent);

}

