package App;

import EventManagement.StubHubEvent;
import TokenManagement.StubHubApiToken;

import java.util.List;

/**
 * Created by jordan on 7/2/17.
 */
public abstract class StubHubEventRetriever {

    public abstract Integer getNumEvents(final StubHubApiToken accessToken);

    public abstract List<StubHubEvent> getEvents(final StubHubApiToken accessToken, final int rowStart, final int eventsPerRequest);

}
