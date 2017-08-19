package App;

import EventManagement.StubHubActiveEventUpdateManager;
import EventManagement.StubHubEventID;
import TokenManagement.StubHubApiAccessToken;
import TokenManagement.StubHubApiToken;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StubHubActiveRecordUpdateManagerImplTest {

    private static final String ACCESS_TOKEN_STRING = "ACCESS_TOKEN";
    private static final LocalDateTime SECONDS_TO_EXPR = LocalDateTime.now();
    private static final String EVENT_ID_1 = "12345";
    private static final String EVENT_ID_2 = "67890";

    private StubHubActiveRecordUpdateManager activeRecordUpdateManager;

    @Mock
    private StubHubActiveEventUpdateManager activeEventUpdateManager;
    @Mock
    private StubHubActiveListingUpdateManager activeListingUpdateManager;

    private StubHubEventID activeEventID_1, activeEventID_2;
    private Collection<StubHubEventID> activeEventIDList;
    private StubHubApiToken accessToken;

    @Test
    /**
     * testUpdate
     * the update method should call the update method from the ActiveEventUpdateManager
     * and subsequently pass the resulting eventID's to the ActiveListingUpdateManager
     */
    public void testUpdate(){
        activeRecordUpdateManager = new StubHubActiveRecordUpdateManagerImpl(activeEventUpdateManager, activeListingUpdateManager);

        accessToken = new StubHubApiAccessToken(ACCESS_TOKEN_STRING, SECONDS_TO_EXPR, StubHubApiScope.PROD);
        activeEventID_1 = new StubHubEventID(EVENT_ID_1);
        activeEventID_2 = new StubHubEventID(EVENT_ID_2);
        activeEventIDList = Arrays.asList(activeEventID_1, activeEventID_2);

        when(activeEventUpdateManager.update(accessToken)).thenReturn(activeEventIDList);
        activeRecordUpdateManager.update(accessToken);
        verify(activeListingUpdateManager).update(accessToken, activeEventIDList);
    }
}