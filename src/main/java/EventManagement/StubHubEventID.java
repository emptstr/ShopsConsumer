package EventManagement;

/**
 * Created by jordan on 6/22/17.
 */
public class StubHubEventID {

    private String eventID;

    public StubHubEventID(String eventID) {
        this.eventID = eventID;
    }

    public boolean equals(final Object object){
        StubHubEventID eventID = (StubHubEventID)object;
        return eventID.getEventID().equals(this.eventID);
    }

    public String getEventID() {
        return eventID;
    }
}
