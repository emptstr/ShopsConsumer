package shpe.consumer.dao;

import shpe.consumer.model.TokenSet;

import java.util.Optional;

/**
 * Created by jordan on 8/21/17.
 */
public abstract class TokenSetDao {

    public abstract Optional<TokenSet> fetchTokenSet(final String tokenSetKey);

    public abstract void persistTokenSet(final String tokenSetKey, final TokenSet toBeSaved);
}
