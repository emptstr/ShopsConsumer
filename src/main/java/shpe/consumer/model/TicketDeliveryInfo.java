package shpe.consumer.model;

import java.util.HashMap;
import java.util.Map;

public enum TicketDeliveryInfo {
    ELECTRONIC(1),
    INSTANT_DOWNLOAD(2),
    LMS(4),
    UPS(5);

    int deliveryTypeCode;

    private TicketDeliveryInfo(int deliveryTypeCode){
        this.deliveryTypeCode = deliveryTypeCode;
    }

    private static Map<Integer, TicketDeliveryInfo> map = new HashMap<Integer, TicketDeliveryInfo>();

    static {
        for (TicketDeliveryInfo legEnum : TicketDeliveryInfo.values()) {
            map.put(legEnum.deliveryTypeCode, legEnum);
        }
    }

    public static TicketDeliveryInfo valueOf(int legNo) {
        return map.get(legNo);
    }
}
