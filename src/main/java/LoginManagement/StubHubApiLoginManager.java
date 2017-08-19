package LoginManagement;

import CredentialManagement.StubHubApiCredentials;
import TokenManagement.StubHubApiRefreshToken;

/**
 * Created by jordan on 6/13/17.
 */
public interface StubHubApiLoginManager {
    StubHubApiLoginResult login(StubHubApiCredentials credentials);

    StubHubApiLoginResult refresh(StubHubApiCredentials credentials, StubHubApiRefreshToken refreshToken);

}

