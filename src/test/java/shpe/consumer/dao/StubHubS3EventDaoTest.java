package shpe.consumer.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jets3t.service.S3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import shpe.consumer.model.StubHubEvent;
import shpe.consumer.serializer.StubHubEventSerializer;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Gson.class, GsonBuilder.class})
public class StubHubS3EventDaoTest {

    private StubHubS3EventDao s3EventDao;
    @Mock
    private S3Service s3Service;
    @Mock
    private S3Bucket eventS3Bucket;
    @Mock
    private StubHubEventSerializer eventSerializer;
    @Mock
    private S3ObjectFactory s3ObjectFactory;
    private Gson gson = PowerMockito.mock(Gson.class);
    private GsonBuilder gsonBuilder = PowerMockito.mock(GsonBuilder.class);

    @Mock
    S3Object eventS3Object;

    private StubHubEvent eventToBeSaved;


    @Test
    public void shouldSaveEventToS3() throws Exception {
        eventToBeSaved = new StubHubEvent(new StubHubEvent.StubHubEventID("1111"), null, null, null, null);
        String eventFileName = String.format("event-%s.txt", eventToBeSaved.getEventID().getEventID());
        String eventJsonString = "json_string";

        s3EventDao = new StubHubS3EventDao(s3Service, eventS3Bucket, gsonBuilder, eventSerializer, s3ObjectFactory);

        when(gsonBuilder.create()).thenReturn(gson);
        when(gson.toJson(eventToBeSaved, StubHubEvent.class)).thenReturn("json_string");
        when(s3ObjectFactory.create(eventFileName, eventJsonString)).thenReturn(eventS3Object);

        s3EventDao.saveEvent(eventToBeSaved);

        verify(s3Service).putObjectAcl(eventS3Bucket, eventS3Object);
        verify(gsonBuilder).registerTypeAdapter(StubHubEvent.class, eventSerializer);
    }
}