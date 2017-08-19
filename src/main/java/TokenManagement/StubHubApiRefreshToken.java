package TokenManagement;

import App.StubHubApiScope;

import java.io.Serializable;

/**
 * Created by jordan on 6/13/17.
 */
public class StubHubApiRefreshToken extends StubHubApiToken {


    public StubHubApiRefreshToken(final String refreshToken, StubHubApiScope scope) {
        super(refreshToken, scope);
    }

}
