package shpe.consumer.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joda.money.Money;
import shpe.consumer.model.TicketDeliveryInfo;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class StubHubListing {
    private final String listingId;
    private final Money currentTicketPriceThroughVenue;
    private final Money currentListingPrice;
    private final int ticketListingQuantity;
    private final TicketDeliveryInfo ticketDeliveryInfo;
    private final boolean isDirtyTicket;
}
