package shpe.consumer.accessor;

import shpe.consumer.model.StubHubEvent;
import shpe.consumer.model.StubHubApiToken;

import java.util.List;

/**
 * Created by jordan on 7/2/17.
 */
public abstract class EventApiAccessor {

    public abstract List<StubHubEvent> getEvents(final StubHubApiToken accessToken, final int rowStart, final int eventsPerRequest);

}
