package EventManagement;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jordan on 7/2/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class StubHubDatabaseEventUpdateManagerImplTest {

    private static final String EVENT_ID_1 = "EVENT_ID_1";
    private static final String EVENT_ID_2 = "EVENT_ID_2";
    private static final LocalDateTime EVENT_1_DATE = LocalDateTime.now();
    private static final LocalDateTime UPDATED_EVENT_1_DATE = EVENT_1_DATE;
    private static final LocalDateTime EVENT_2_DATE = LocalDateTime.now();
    private static final LocalDateTime UPDATED_EVENT_2_DATE = LocalDateTime.now().plusDays(1);

    private StubHubDatabaseEventUpdateManager databaseEventUpdateManager;
    @Mock
    private StubHubEventDao eventDao;
    @Mock
    private StubHubVenue venue1, venue2;
    @Mock
    private StubHubAncestors ancestors1, ancestors2;

    private StubHubEvent event1, event2, inputEvent1, inputEvent2;
    private List<StubHubEvent> inputEventList;

    @Before
    public void setUp(){
        databaseEventUpdateManager = new StubHubDatabaseEventUpdateManagerImpl(eventDao);
        event1 = new StubHubEvent(new StubHubEventID(EVENT_ID_1), EventStatus.Active, EVENT_1_DATE, venue1, ancestors1);
        event2 = new StubHubEvent(new StubHubEventID(EVENT_ID_2), EventStatus.Active, EVENT_2_DATE, venue2, ancestors2);
        inputEvent1 = new StubHubEvent(new StubHubEventID(EVENT_ID_1), EventStatus.Active, UPDATED_EVENT_1_DATE, venue1, ancestors1);
        inputEvent2 = new StubHubEvent(new StubHubEventID(EVENT_ID_2), EventStatus.Active, UPDATED_EVENT_2_DATE, venue2, ancestors2);
        inputEventList = new ArrayList<>(Arrays.asList(inputEvent1, inputEvent2));
    }

    @Test
    public void update() throws Exception {

        when(eventDao.getEvent(inputEvent1.getEventID())).thenReturn(event1);
        when(eventDao.getEvent(inputEvent2.getEventID())).thenReturn(event2);

        databaseEventUpdateManager.update(inputEventList);

        assertEquals(event1.getEventDateUTC(), EVENT_1_DATE);
        assertEquals(event2.getEventDateUTC(), UPDATED_EVENT_2_DATE);
        verify(eventDao).saveEvent(event1);
        verify(eventDao).saveEvent(event2);
    }
}