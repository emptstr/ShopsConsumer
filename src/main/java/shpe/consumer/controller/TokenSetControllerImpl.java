package shpe.consumer.controller;

import lombok.RequiredArgsConstructor;
import shpe.consumer.dao.TokenSetDao;
import shpe.consumer.generator.TokenSetGenerator;
import shpe.consumer.model.TokenSet;
import shpe.consumer.predicate.IsTokenSetValidPredicate;

import java.util.Optional;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class TokenSetControllerImpl extends TokenSetController {

    private static final Logger logger = Logger.getLogger(TokenSetControllerImpl.class.getName());
    private static final String TOKEN_SET_PATH = "/token-set.ser";

    private final TokenSetDao tokenDao;
    private final TokenSetRefreshController accessTokenRefreshController;
    private final TokenSetGenerator tokenSetGenerator;
    private final IsTokenSetValidPredicate isTokenSetValidPredicate;

    @Override
    public TokenSet retrieveTokenSet() {
        return retrieveValidTokenSet();
    }

    private TokenSet retrieveValidTokenSet() {
        logger.info("Started retrieving token set...");
        Optional<TokenSet> tokenSetOptional = tokenDao.fetchTokenSet(TOKEN_SET_PATH);
        if (tokenSetOptional.isPresent()) {
            TokenSet tokenSet = tokenSetOptional.get();
            if (!isTokenSetValidPredicate.test(tokenSet)) {
               tokenSet =  refreshExpiredAccessToken(tokenSet);
            }
            logger.info("Finished retrieving token set.");
            return tokenSet;
        }
        logger.info("Finished retrieving token set.");
        return createAccessTokenForTheFirstTime();
    }


    private TokenSet refreshExpiredAccessToken(final TokenSet tokenSet) {
        logger.info("Refreshing expired access token...");
        TokenSet accessToken = accessTokenRefreshController.refreshExpiredTokenSet(tokenSet);
        tokenDao.persistTokenSet(TOKEN_SET_PATH, accessToken);
        logger.info("Finished refreshing expired access token");
        return accessToken;
    }

    private TokenSet createAccessTokenForTheFirstTime() {
        logger.info("Creating access token...");
        TokenSet newTokens = tokenSetGenerator.generateNewTokenSet();
        tokenDao.persistTokenSet(TOKEN_SET_PATH, newTokens);
        logger.info("Finished creating access token.");
        return newTokens;
    }
}