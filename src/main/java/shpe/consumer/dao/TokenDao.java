package shpe.consumer.dao;

import shpe.consumer.model.StubHubApiToken;

/**
 * Created by jordan on 8/19/17.
 */
public abstract class TokenDao {

    public abstract StubHubApiToken fetchToken(final String tokenKey);

    public abstract void persistToken(final String tokenKey, final StubHubApiToken apiToken);
}
