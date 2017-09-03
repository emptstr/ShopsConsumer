package shpe.consumer.factory;

import shpe.consumer.model.StubHubApiToken;

/**
 * Created by jordan on 8/19/17.
 */
public interface TokenFactory {

    public StubHubApiToken createToken(final String tokenString, final long secondsTillTokenExpires);
}
