package EventManagement;

import java.util.List;

/**
 * Created by jordan on 7/2/17.
 */
public class StubHubDatabaseEventUpdateManagerImpl extends StubHubDatabaseEventUpdateManager {

    private final StubHubEventDao eventDao;

    public StubHubDatabaseEventUpdateManagerImpl(StubHubEventDao eventDao) {
        this.eventDao = eventDao;
    }

    @Override
    public void update(List<StubHubEvent> eventList_1) {
        for(StubHubEvent event: eventList_1){
            StubHubEvent storedEvent = eventDao.getEvent(event.getEventID());
            if(!event.getEventDateUTC().equals(storedEvent.getEventDateUTC())){
                storedEvent.setEventDateUTC(event.getEventDateUTC());
            }
            eventDao.saveEvent(storedEvent);
        }
    }
}
