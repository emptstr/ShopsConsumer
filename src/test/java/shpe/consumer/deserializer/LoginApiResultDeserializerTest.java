package shpe.consumer.deserializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;
import shpe.consumer.model.StubHubApiLoginResult;

import static org.junit.Assert.assertEquals;

/**
 * Created by jordan on 11/19/17.
 */
public class LoginApiResultDeserializerTest {

    private static final String TO_BE_DESERIALIZED = "{\n" +
            "    \"access_token\": \"849c9bef-9eac-36e5-b8f3-39fd30329f2b\",\n" +
            "    \"refresh_token\": \"59eb69ab-3c85-3454-9d63-78057be8c28c\",\n" +
            "    \"scope\": \"default\",\n" +
            "    \"token_type\": \"Bearer\",\n" +
            "    \"expires_in\": 15549445\n" +
            "}";

    private static final StubHubApiLoginResult EXPECTED_RESULT =  new StubHubApiLoginResult("849c9bef-9eac-36e5-b8f3-39fd30329f2b",
            "59eb69ab-3c85-3454-9d63-78057be8c28c", 15549445);

    @Test
    public void shouldDeserialze(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(StubHubApiLoginResult.class, new LoginApiResultDeserializer());
        Gson gson = gsonBuilder.create();
        assertEquals(EXPECTED_RESULT, gson.fromJson(TO_BE_DESERIALIZED, StubHubApiLoginResult.class));
    }

}