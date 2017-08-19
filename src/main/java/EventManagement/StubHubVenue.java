package EventManagement;

/**
 * Represents a venue at which StubHub events are hosted
 * @author Jordan Gaston
 * @version 0.1.17
 */
public class StubHubVenue {
    private final String venueID;
    private final String city;
    private final String state;
    private final String postalCode;
    private final String country;

    public StubHubVenue(String venueID, String city, String state, String postalCode, String country) {
        this.venueID = venueID;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
    }

    public String getVenueID() {
        return venueID;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    @Override
    /**
     * equals
     * should return true if the venue ID's are identical
     */
    public boolean equals(final Object obj){
        StubHubVenue venue = (StubHubVenue) obj;
        return venueID.equals(venue.getVenueID());
    }

    public String getCountry() {
        return country;
    }
}
