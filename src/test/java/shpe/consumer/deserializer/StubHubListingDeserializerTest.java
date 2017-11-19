package shpe.consumer.deserializer;

import shpe.consumer.deserializer.StubHubListingDeserializer;
import shpe.consumer.model.StubHubListing;
import shpe.consumer.model.TicketDeliveryInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.Assert.assertEquals;

/**
 * Created by jordan on 9/1/17.
 */
public class StubHubListingDeserializerTest {

    private final String jsonListing = "{\n" +
            "    \"eventId\": 9770911,\n" +
            "    \"totalListings\": 14,\n" +
            "    \"totalTickets\": 61,\n" +
            "    \"minQuantity\": 2,\n" +
            "    \"maxQuantity\": 7,\n" +
            "    \"listing\": [\n" +
            "        {\n" +
            "            \"listingId\": 1239175625,\n" +
            "            \"currentPrice\": {\n" +
            "                \"amount\": 63.81,\n" +
            "                \"currency\": \"USD\"\n" +
            "            },\n" +
            "            \"listingPrice\": {\n" +
            "                \"amount\": 48,\n" +
            "                \"currency\": \"USD\"\n" +
            "            },\n" +
            "            \"sectionId\": 1402183,\n" +
            "            \"row\": \"NN\",\n" +
            "            \"quantity\": 2,\n" +
            "            \"sellerSectionName\": \"BAL\",\n" +
            "            \"sectionName\": \"Balcony\",\n" +
            "            \"seatNumbers\": \"101,102\",\n" +
            "            \"zoneId\": 221451,\n" +
            "            \"zoneName\": \"Balcony\",\n" +
            "            \"deliveryTypeList\": [\n" +
            "                5\n" +
            "            ],\n" +
            "            \"deliveryMethodList\": [\n" +
            "                22,\n" +
            "                23,\n" +
            "                24,\n" +
            "                25\n" +
            "            ],\n" +
            "            \"isGA\": 0,\n" +
            "            \"dirtyTicketInd\": false,\n" +
            "            \"splitOption\": \"2\",\n" +
            "            \"ticketSplit\": \"1\",\n" +
            "            \"splitVector\": [\n" +
            "                2\n" +
            "            ],\n" +
            "            \"sellerOwnInd\": 0\n" +
            "        }],\n" +
            "    \"listingAttributeCategorySummary\": [],\n" +
            "    \"deliveryTypeSummary\": [],\n" +
            "    \"start\": 0,\n" +
            "    \"rows\": 14\n" +
            "}";

            @Test
            public void shouldDeserializeListing() throws Exception {
                Money currentTicketPrice = Money.of(CurrencyUnit.USD, new BigDecimal(63.81).setScale(2, RoundingMode.DOWN));
                Money currentListingPrice = Money.of(CurrencyUnit.USD, new BigDecimal(48).setScale(2, RoundingMode.CEILING));
                StubHubListing listing = new StubHubListing("1239175625",currentTicketPrice, currentListingPrice,2, TicketDeliveryInfo.UPS, false);

                StubHubListingDeserializer listingDeserializer = new StubHubListingDeserializer();

                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(StubHubListing[].class, new StubHubListingDeserializer());
                Gson gson = gsonBuilder.create();

                StubHubListing actualListing = gson.fromJson(jsonListing, StubHubListing[].class)[0];

                assertEquals(listing, actualListing);
            }
}