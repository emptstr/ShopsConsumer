package EventManagement;

import org.joda.time.LocalDateTime;

/**
 * Created by jordan on 6/13/17.
 */
public class StubHubEvent {

    private StubHubEventID eventID; //TODO remove this data object
    private EventStatus eventStatus;
    private LocalDateTime eventDateUTC;
    private StubHubVenue venue;
    private  StubHubAncestors ancestor;

    public StubHubEvent(){

    }

    public StubHubEvent(final StubHubEventID eventID, final EventStatus eventStatus, final LocalDateTime eventDateUTC, final StubHubVenue venue, final StubHubAncestors ancestor) {
        this.eventID = eventID;
        this.eventStatus = eventStatus;
        this.eventDateUTC = eventDateUTC;
        this.venue = venue;
        this.ancestor = ancestor;
    }

    public StubHubEventID getEventID() {
        return eventID;
    }

    public boolean Equals(Object obj){
        StubHubEvent event = (StubHubEvent) obj;
        return eventID.equals(event);
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public LocalDateTime getEventDateUTC() {
        return eventDateUTC;
    }

    public StubHubVenue getVenue() {
        return venue;
    }

    public StubHubAncestors getAncestor() {
        return ancestor;
    }

    public void setEventDateUTC(final LocalDateTime updatedDate){
        this.eventDateUTC = updatedDate;
    }

    public void setEventStatus(final EventStatus updatedStatus){
        this.eventStatus = updatedStatus;
    }
}
