package EventManagement;

import TokenManagement.StubHubApiToken;

import java.util.Collection;

/**
 * Created by jordan on 6/22/17.
 */
public abstract class StubHubActiveEventUpdateManager {

    public abstract Collection<StubHubEventID> update(StubHubApiToken accessToken);
}
