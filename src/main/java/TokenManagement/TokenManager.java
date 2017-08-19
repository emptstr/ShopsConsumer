package TokenManagement;

import App.StubHubApiScope;

/**
 * TokenManager
 * manages all token operations for the application
 * @see StubHubApiAccessToken
 * @see StubHubApiRefreshToken
 * @author Jordan Gaston
 * @version 0.1.17
 */
public abstract class TokenManager {

    /**
     * getAccessToken
     * returns the Access Token for the api based on the provided scope object
     * @param scope - the scope of access token to be retrieved
     * @return StubHubApiAccessToken - the current access token or NULL if one does not already exist
     */
    public abstract StubHubApiAccessToken getAccessToken(final StubHubApiScope scope);

    /**
     * isExpired
     * returns TRUE if the Access Token is expired - FALSE otherwise
     * @param accessToken
     */
    public abstract boolean isExpired(final StubHubApiAccessToken accessToken);

    /**
     * createTokens
     * creates an Access Token
     * @param accessTokenString - a string representing an access token for the StubHubApi
     * @param refreshTokenString - a string representing a refresh token for the StubHubApi
     * @param secondsTillTokenExp - the number of seconds until the expiration of the access token at which point it must be refreshed
     * @param accessTokenScope - the scope of the token being created - can be sandbox or prod
     * @return StubHubApiAccessToken - a new access token
     */
    public abstract StubHubApiAccessToken createTokens(final String accessTokenString, final  String refreshTokenString,
                                                       final int secondsTillTokenExp, final StubHubApiScope accessTokenScope);

    /**
     * getRefreshToken
     * returns the Refresh Token for the api based on the provided scope object
     * @param scope - the scope of the refresh token to be returned
     * @return StubHubApiRefreshToken - the current application refresh tokekn or NULL if one does not exist
     */
    public abstract StubHubApiRefreshToken getRefreshToken(StubHubApiScope scope);
}


