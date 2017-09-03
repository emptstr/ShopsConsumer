package shpe.consumer.deserializer;

import shpe.consumer.model.StubHubListing;
import shpe.consumer.model.TicketDeliveryInfo;
import com.google.gson.*;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by jordan on 9/1/17.
 */
public class StubHubListingDeserializer implements JsonDeserializer<StubHubListing[]> {

    private static final String LISTINGS = "listing";
    private static final String LISTING_ID = "listingId";
    private static final String CURRENT_PRICE = "currentPrice";
    private static final String LISTING_PRICE = "listingPrice";
    private static final String QUANTITY = "quantity";
    private static final String DELIVERY_TYPE_LIST = "deliveryTypeList";
    private static final String DIRTY_TICKET_IND = "dirtyTicketInd";

    @Override
    public StubHubListing[] deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonObject responseObject = jsonElement.getAsJsonObject();
        final JsonArray arrayOfListings = responseObject.get(LISTINGS).getAsJsonArray();

        StubHubListing[] listings = new StubHubListing[arrayOfListings.size()];

        for(int i =0; i < listings.length; i++){
            final JsonObject jsonEvent = arrayOfListings.get(i).getAsJsonObject();
            listings[i] = (jsonToListing(jsonEvent));
        }
        return listings;
    }

    private StubHubListing jsonToListing(JsonObject jsonObject){
        String listingId = jsonObject.get(LISTING_ID).toString();
        Money currentEventTicketPrice = getCurrentTicketPrice(jsonObject);
        Money currentListingPrice = getCurrentListingPrice(jsonObject);
        int quantity = jsonObject.get(QUANTITY).getAsInt();
        TicketDeliveryInfo ticketDeliveryInfo = getTicketDeliveryInfo(jsonObject);
        boolean dirtyTicketInfo = jsonObject.get(DIRTY_TICKET_IND).getAsBoolean();
        return new StubHubListing(listingId, currentEventTicketPrice, currentListingPrice, quantity, ticketDeliveryInfo, dirtyTicketInfo);
    }

    private TicketDeliveryInfo getTicketDeliveryInfo(JsonObject jsonObject) {
        JsonArray listingDeliveryList = jsonObject.get(DELIVERY_TYPE_LIST).getAsJsonArray();
        int deliveryTypeInt = listingDeliveryList.get(0).getAsInt();
        return TicketDeliveryInfo.valueOf(deliveryTypeInt);
    }

    private Money getCurrentListingPrice(JsonObject jsonObject) {
        JsonObject currentListingPriceBlob = jsonObject.get(LISTING_PRICE).getAsJsonObject();
        BigDecimal currentListingPrice = currentListingPriceBlob.get("amount").getAsBigDecimal();
        String listingCurrencyCode = currentListingPriceBlob.get("currency").getAsString();
        return Money.of(CurrencyUnit.getInstance(listingCurrencyCode), currentListingPrice.setScale(2, RoundingMode.CEILING));
    }

    private Money getCurrentTicketPrice(JsonObject jsonObject) {
        JsonObject currenyJsonBlob = jsonObject.getAsJsonObject(CURRENT_PRICE);
        BigDecimal currentPrice = currenyJsonBlob.get("amount").getAsBigDecimal();
        String currencyCode = currenyJsonBlob.get("currency").getAsString();
        return Money.of(CurrencyUnit.getInstance(currencyCode), currentPrice.setScale(2, RoundingMode.CEILING));
    }
}
