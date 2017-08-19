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
public class StubHubApiAccessToken extends StubHubApiToken{

    private final LocalDateTime secondsTooExpr;

    public StubHubApiAccessToken(final String accessTokenString, final LocalDateTime secondsTooExpr, final StubHubApiScope scope) {
        super(accessTokenString, scope);
        this.secondsTooExpr = secondsTooExpr;
    }

    public LocalDateTime getSecondsToExpr() {
        return secondsTooExpr;
    }

}
