package shpe.consumer.dao;

import shpe.consumer.model.StubHubEvent;

/**
 * Created by jordan on 7/2/17.
 *
 */
public abstract class StubHubEventDao {

    public abstract StubHubEvent getEvent(final StubHubEvent.StubHubEventID eventId);

    public abstract void saveEvent(StubHubEvent event1);

}

