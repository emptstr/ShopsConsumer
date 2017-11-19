package shpe.consumer.controller;

import shpe.consumer.model.TokenSet;

/**
 * Created by jordan on 8/21/17.
 */
public abstract class TokenSetRefreshController {

    public abstract TokenSet refreshExpiredTokenSet(final TokenSet tokenSet);
}
