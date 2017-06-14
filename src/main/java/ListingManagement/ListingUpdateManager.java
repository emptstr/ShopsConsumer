package ListingManagement;

import EventManagement.StubHubEvent;
import TokenManagement.StubHubApiAccessToken;

import java.util.List;

/**
 * Created by jordan on 6/13/17.
 */
public interface ListingUpdateManager {
    void update(StubHubApiAccessToken accessToken, List<StubHubEvent> events);
}
