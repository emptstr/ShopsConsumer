package shpe.consumer.dao;

import shpe.consumer.model.TokenSet;

/**
 * Created by jordan on 8/21/17.
 */
public abstract class TokenSetDao {

    public abstract TokenSet fetchTokenSet(final String tokenSetKey);

    public abstract void persistTokenSet(final String tokenSetKey, final TokenSet toBeSaved);
}
