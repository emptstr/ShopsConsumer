package TokenManagement;

import App.StubHubApiScope;

/**
 * TokenManager
 * - manages all token operations for the application
 */
public interface TokenManager {

    /**
     * getAccessToken
     * - returns the Access Token for the api based on the provided scope object
     */
    StubHubApiAccessToken getAccessToken(StubHubApiScope scope);

    /**
     * isExpired
     * - returns TRUE if the Access Token is expired - FALSE otherwise
     */
    boolean isExpired(StubHubApiAccessToken accessToken_1);

    /**
     * createToken
     * - creates an Access Token
     */
    StubHubApiAccessToken createToken(String accessToken1, String refreshToken, int secondsTillTokenExp, StubHubApiScope scope_2);

    /**
     * getRefreshToken
     * - returns the Refresh Token for the api based on the provided scope object
     */
    StubHubApiRefreshToken getRefreshToken(StubHubApiScope scope);
}


