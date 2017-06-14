package TokenManagement;

import App.StubHubApiScope;
import org.joda.time.LocalDateTime;

import java.io.Serializable;

/**
 * StubHubApiAccessToken
 * - represents the access token returned by the StubHubApi login endpoint
 * - must be included in the Authorization header for many StubHub Api requests
 * - can be Sandbox or Production Scope
 * - expires 6 month's after being issued
 */
public class StubHubApiAccessToken implements Serializable{

    private final String accessTokenString;
    private final LocalDateTime secondsTooExpr;
    private final StubHubApiScope scope;

    public StubHubApiAccessToken(final String accessTokenString, final LocalDateTime secondsTooExpr, final StubHubApiScope scope) {
        this.accessTokenString = accessTokenString;
        this.secondsTooExpr = secondsTooExpr;
        this.scope = scope;
    }

    public String getAccessTokenString() {
        return accessTokenString;
    }

    public LocalDateTime getSecondsToExpr() {
        return secondsTooExpr;
    }

    public boolean isProd() {
        return scope.equals(StubHubApiScope.PROD);
    }
}
