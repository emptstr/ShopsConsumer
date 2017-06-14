package EventManagement;


import TokenManagement.StubHubApiAccessToken;

import java.util.List;

/**
 * Created by jordan on 6/8/17.
 */
public interface EventUpdateManager {

    public List<StubHubEvent> update(StubHubApiAccessToken events);

}
