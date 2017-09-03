package shpe.consumer.serializer;

import com.google.gson.*;
import shpe.consumer.model.StubHubEvent;
import shpe.consumer.model.StubHubListing;
import shpe.consumer.model.StubHubListingCollection;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import static shpe.consumer.model.StubHubEvent.StubHubAncestors;
import static shpe.consumer.model.StubHubEvent.StubHubAncestors.*;
import static shpe.consumer.model.StubHubEvent.StubHubVenue;

public class StubHubEventSerializer implements JsonSerializer<StubHubEvent>{
    @Override
    public JsonElement serialize(StubHubEvent stubHubEvent, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject venue = stringifyVenue(stubHubEvent.getVenue());
        JsonObject ancestors = stringifyAncestors(stubHubEvent.getAncestor());
        JsonArray allEventListings = stringifyAllEventListings(stubHubEvent.getAllEventListings());
        JsonObject serializedEvent = buildEvent(stubHubEvent, venue, ancestors, allEventListings);
        return serializedEvent;
    }

    private JsonObject buildEvent(StubHubEvent stubHubEvent, JsonObject venue, JsonObject ancestors, JsonArray allEventListings) {
        JsonObject serializedEvent = new JsonObject();
        serializedEvent.addProperty("eventId", stubHubEvent.getEventID().getEventID());
        serializedEvent.addProperty("eventStatus", stubHubEvent.getEventStatus().name());
        serializedEvent.addProperty("eventDateUTC", stubHubEvent.getEventDateUTC().toString());
        serializedEvent.add("venue", venue);
        serializedEvent.add("ancestors", ancestors);
        serializedEvent.add("listings", allEventListings);
        return serializedEvent;
    }

    private JsonObject stringifyVenue(StubHubVenue venue) {
        String venueID = venue.getVenueID();
        String city = venue.getCity();
        String state = venue.getState();
        String postalCode = venue.getPostalCode();
        String country = venue.getCountry();
        JsonObject serializedVenue = new JsonObject();
        serializedVenue.addProperty("venueId", venueID);
        serializedVenue.addProperty("city", city);
        serializedVenue.addProperty("state",state);
        serializedVenue.addProperty("postalCode", postalCode);
        serializedVenue.addProperty("country", country);
        return serializedVenue;
    }

    private JsonObject stringifyAncestors(StubHubAncestors stubHubEvent) {
        JsonObject serializedAncestors = new JsonObject();
        serializedAncestors.add("categories", getCategoriesArray(stubHubEvent.getCategories()));
        serializedAncestors.add("groupings", getGroupingsArray(stubHubEvent.getGroupings()));
        serializedAncestors.add("performers", getPerformersArray(stubHubEvent.getPerformers()));
        return serializedAncestors;
    }

    private JsonArray getCategoriesArray(List<StubHubAncestors.StubHubCategory> categories) {
       JsonArray serializedCategories = new JsonArray();
        for (StubHubCategory category : categories) {
            serializedCategories.add(category.getCategoryID());
        }
        return serializedCategories;
    }

    private JsonArray getGroupingsArray(List<StubHubEvent.StubHubAncestors.StubHubGrouping> groupings) {
        JsonArray serializedGroupings =  new JsonArray();
        for (StubHubGrouping grouping : groupings) {
            serializedGroupings.add(grouping.getGroupingID());
        }
        return serializedGroupings;
    }

    private JsonArray getPerformersArray(List<StubHubEvent.StubHubAncestors.StubHubPerformer> performers) {
        JsonArray serializedPerformers = new JsonArray();
        for (StubHubPerformer performer : performers) {
            serializedPerformers.add(performer.getPerformerID());
        }
        return serializedPerformers;
    }

    private JsonArray stringifyAllEventListings(Collection<StubHubListingCollection> stubHubEvent) {
        JsonArray listingCollections = new JsonArray();
        for (StubHubListingCollection listingCollection : stubHubEvent) {
            listingCollections.add(getListingCollection(listingCollection));
        }
        return listingCollections;
    }

    private JsonElement getListingCollection(StubHubListingCollection listingCollection) {
        JsonObject serializedListingCollection = new JsonObject();
        JsonArray listingCollectionArray = new JsonArray();
        String listingDate = listingCollection.getRetrievalDate().toString();
        for (StubHubListing listing : listingCollection.getListings()) {
            listingCollectionArray.add(buildListing(listing));
        }
        serializedListingCollection.addProperty("listingDate", listingDate);
        serializedListingCollection.add("listings", listingCollectionArray);
        return serializedListingCollection;
    }

    private JsonElement buildListing(StubHubListing listing) {
        JsonObject serializedListing = new JsonObject();
        serializedListing.addProperty("listingId",listing.getListingId());
        serializedListing.addProperty("currencyCode",
                listing.getCurrentListingPrice().getCurrencyUnit().getCurrencyCode());
        serializedListing.addProperty("currentListingPrice", listing.getCurrentListingPrice().getAmount());
        serializedListing.addProperty("currentTicketPrice", listing.getCurrentTicketPriceThroughVenue().getAmount());
        serializedListing.addProperty("quantity", listing.getTicketListingQuantity());
        serializedListing.addProperty("ticketDeliveryInfo", listing.getTicketDeliveryInfo().name());
        serializedListing.addProperty("isDirtyTicket", listing.isDirtyTicket());
        return serializedListing;
    }
}
