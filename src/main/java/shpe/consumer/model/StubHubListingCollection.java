package shpe.consumer.model;

import org.joda.time.DateTime;

import java.util.Collection;

public final class StubHubListingCollection {
    private final DateTime collectionRetrievalDate;
    private final Collection<StubHubListing> listings;

    public StubHubListingCollection(DateTime collectionRetrievalDate, Collection<StubHubListing> listings) {
        this.collectionRetrievalDate = collectionRetrievalDate;
        this.listings = listings;
    }

    public DateTime getRetrievalDate(){
        return new DateTime(collectionRetrievalDate.toDateTime());
    }

    public Collection<StubHubListing> getListings(){
        return listings;
    }
}
