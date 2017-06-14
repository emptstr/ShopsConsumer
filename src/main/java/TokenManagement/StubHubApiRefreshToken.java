package TokenManagement;

import App.StubHubApiScope;

import java.io.Serializable;

/**
 * Created by jordan on 6/13/17.
 */
public class StubHubApiRefreshToken implements Serializable {

    private final String refreshTokenString;
    private final StubHubApiScope scope;

    public StubHubApiRefreshToken(final String refreshToken, StubHubApiScope scope) {
        this.refreshTokenString = refreshToken;
        this.scope = scope;
    }

    public String getRefreshTokenString() {
        return refreshTokenString;
    }

    public boolean isProd() {
        return scope.equals(StubHubApiScope.PROD);
    }
}
