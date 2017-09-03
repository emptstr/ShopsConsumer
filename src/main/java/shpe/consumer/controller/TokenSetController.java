package shpe.consumer.controller;

import shpe.consumer.model.StubHubApiToken;
import shpe.consumer.model.TokenSet;

/**
 * TokenSetController
 * @see StubHubApiToken
 * @author Jordan Gaston
 * @version 0.1.17
 */
public abstract class TokenSetController {

    /**
     * retrieveTokenSet
     * returns a valid token set to facilitate access to an API
     * @return TokenSet
     */
    public abstract TokenSet retrieveTokenSet();
}


