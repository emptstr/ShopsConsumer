package shpe.consumer.controller;

import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import shpe.consumer.controller.ActiveEventUpdateControllerImpl;
import shpe.consumer.controller.EventUpdateController;
import shpe.consumer.controller.EventListingUpdateController;
import shpe.consumer.controller.ActiveEventUpdateController;
import shpe.consumer.dao.StubHubEventDao;
import shpe.consumer.model.StubHubApiToken;
import shpe.consumer.model.StubHubEvent;

import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActiveEventUpdateControllerImplTest {

    private static final String ACCESS_TOKEN_STRING = "ACCESS_TOKEN";
    private static final LocalDateTime SECONDS_TO_EXPR = LocalDateTime.now();

    private ActiveEventUpdateController activeRecordUpdateManager;

    @Mock
    private EventUpdateController activeEventUpdateManager;
    @Mock
    private EventListingUpdateController activeListingUpdateManager;
    @Mock
    private StubHubEventDao eventDao;
    @Mock
    private StubHubEvent event1, event2;


    private StubHubApiToken accessToken;

    @Test
    /**
     * testUpdate
     * the update method should call the update method from the ActiveEventUpdateManager
     * and subsequently pass the resulting eventID's to the ActiveListingUpdateManager
     */
    public void testUpdate(){
        activeRecordUpdateManager = new ActiveEventUpdateControllerImpl(activeEventUpdateManager, activeListingUpdateManager, eventDao);

        accessToken = new StubHubApiToken(ACCESS_TOKEN_STRING, SECONDS_TO_EXPR);
        when(activeEventUpdateManager.update(accessToken)).thenReturn(Arrays.asList(event1, event2));
        when(activeListingUpdateManager.update(accessToken, Arrays.asList(event1, event2))).thenReturn(Arrays.asList(event1,event2));
        activeRecordUpdateManager.update(accessToken);

        verify(eventDao).saveEvent(event1);
        verify(eventDao).saveEvent(event2);
    }
}