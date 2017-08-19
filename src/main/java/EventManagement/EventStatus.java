package EventManagement;

/**
 * EventStatus
 * the status of a StubHub event
 * {@see https://developer.stubhub.com/store/site/pages/doc-viewer.jag?category=Search&api=EventSearchAPI&endpoint=searchforeventsv3&version=v3}
 */
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
