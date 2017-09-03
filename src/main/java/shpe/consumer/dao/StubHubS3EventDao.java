package shpe.consumer.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import org.jets3t.service.S3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import shpe.consumer.model.StubHubEvent;
import shpe.consumer.serializer.StubHubEventSerializer;

/**
 * Created by jordan on 9/2/17.
 */
@RequiredArgsConstructor
public class StubHubS3EventDao extends StubHubEventDao {
    private final static String EVENT_FILE_FORMAT_STRING = "event-%s.txt";
    private final S3Service s3Service;
    private final S3Bucket eventBucket;
    private final GsonBuilder gsonBuilder;
    private final StubHubEventSerializer eventSerializer;
    private final S3ObjectFactory s3ObjectFactory;

    @Override
    public StubHubEvent getEvent(StubHubEvent.StubHubEventID eventId) {
        return null;
    }

    @Override
    public void saveEvent(StubHubEvent event1) {
        try {
            gsonBuilder.registerTypeAdapter(StubHubEvent.class, eventSerializer);
            Gson gson = gsonBuilder.create();
            String serializedEvent = gson.toJson(event1, StubHubEvent.class);
            S3Object s3EventObject = s3ObjectFactory.create(String.format(EVENT_FILE_FORMAT_STRING,
                    event1.getEventID().getEventID()), serializedEvent);
            s3Service.putObjectAcl(eventBucket, s3EventObject);
        } catch (Exception e) {
            throw new RuntimeException("Failed while saving event to s3", e);
        }
    }
}
