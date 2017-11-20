package shpe.consumer.deserializer;

import com.google.gson.*;
import shpe.consumer.model.StubHubApiLoginResult;

import java.lang.reflect.Type;

public class LoginApiResultDeserializer implements JsonDeserializer<StubHubApiLoginResult> {

    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String TIME_TILL_EXPIRE = "expires_in";

    @Override
    public StubHubApiLoginResult deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonObject responseObject = jsonElement.getAsJsonObject();
        String accessToken = responseObject.get(ACCESS_TOKEN).getAsString();
        String refreshToken = responseObject.get(REFRESH_TOKEN).getAsString();
        int timeToExpire = responseObject.get(TIME_TILL_EXPIRE).getAsInt();
        return new StubHubApiLoginResult(accessToken, refreshToken, timeToExpire);
    }
}

