package shpe.consumer.accessor;
import shpe.consumer.model.StubHubApiLoginResult;
import shpe.consumer.model.StubHubApiToken;

/**
 * Created by jordan on 6/13/17.
 */
public interface LoginApiAccessor {
    StubHubApiLoginResult login();

    StubHubApiLoginResult refresh(StubHubApiToken refreshToken);

}

