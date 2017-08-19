package App;

import EventManagement.StubHubEventID;
import TokenManagement.StubHubApiToken;

import java.util.Collection;

/**
 * Created by jordan on 6/22/17.
 */
public abstract class StubHubActiveListingUpdateManager {

    public abstract void update(StubHubApiToken accessToken, Collection<StubHubEventID> activeEventIDList);
}
