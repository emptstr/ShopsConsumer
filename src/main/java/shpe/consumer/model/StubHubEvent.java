package shpe.consumer.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode
public class StubHubEvent {

    private StubHubEventID eventID; //TODO remove this data object
    private EventStatus eventStatus;
    private LocalDateTime eventDateUTC;
    private StubHubVenue venue;
    private StubHubAncestors ancestor;
    private List<StubHubListingCollection> allEventListings;

    public StubHubEvent(){

    }

    public StubHubEvent(final StubHubEventID eventID, final EventStatus eventStatus, final LocalDateTime eventDateUTC, final StubHubVenue venue, final StubHubAncestors ancestor) {
        this.eventID = eventID;
        this.eventStatus = eventStatus;
        this.eventDateUTC = eventDateUTC;
        this.venue = venue;
        this.ancestor = ancestor;
        allEventListings = new ArrayList<>();
    }

    public StubHubEventID getEventID() {
        return eventID;
    }

    public boolean Equals(Object obj){
        StubHubEvent event = (StubHubEvent) obj;
        return eventID.equals(event);
    }

    public void setEventDateUTC(final LocalDateTime updatedDate){
        this.eventDateUTC = updatedDate;
    }

    public void setEventStatus(final EventStatus updatedStatus){
        this.eventStatus = updatedStatus;
    }

    public void addUpdatedListings(StubHubListingCollection listingCollection1) {
        allEventListings.add(listingCollection1);
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    @Getter
    public static class StubHubEventID {
        private final String eventID;
    }

    @Getter
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class StubHubVenue {
        private final String venueID;
        private final String city;
        private final String state;
        private final String postalCode;
        private final String country;
    }

    @Getter
    @RequiredArgsConstructor
    public static class StubHubAncestors {

        private final List<StubHubCategory> categories;
        private final List<StubHubGrouping> groupings;
        private final List<StubHubPerformer> performers;

        @RequiredArgsConstructor
        @Getter
        public static class StubHubCategory {
            private final String categoryID;
        }

        @RequiredArgsConstructor
        @Getter
        public static class StubHubGrouping {
            private final String groupingID;
        }

        @Getter
        @RequiredArgsConstructor
        public  static class StubHubPerformer {
            private final String performerID;
        }

    }

    public enum EventStatus {
        Active("Active"), // the event that has not yet taken place
        Completed("Completed"), // the event has already taken place
        Scheduled("Scheduled"), // the event has been scheduled, but it's too far in the future
        Contigent("Contingent"), // the event which is contingent on the outcome of another event (ex. a playoff game)
        Postponed("Postponed"), // the event has been postponed
        Cancelled("Cancelled"); // the event has been cancelled

        String name;

        private EventStatus(final String name) {
            this.name = name;
        }
    }
}
