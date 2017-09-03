package shpe.consumer.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shpe.consumer.accessor.LoginApiAccessor;
import shpe.consumer.model.StubHubApiLoginResult;
import shpe.consumer.model.TokenSet;

/**
 * Created by jordan on 9/3/17.
 */
@RequiredArgsConstructor
public class TokenSetRefreshControllerImpl extends TokenSetRefreshController {

    private static final Logger logger = LoggerFactory.getLogger(TokenSetRefreshControllerImpl.class);
    private final TokenSetFactory tokenSetFactory;
    private final LoginApiAccessor loginManager;

    @Override
    public TokenSet refreshExpiredTokenSet(TokenSet tokenSet) {
        logger.info("Access token has expired");
        StubHubApiLoginResult refreshResult = loginManager.refresh(tokenSet.getRefreshToken());
        TokenSet refreshedTokenSet = tokenSetFactory.createTokens(refreshResult.getAccessTokenString(), refreshResult.getRefreshTokenString(), refreshResult.getSecondsToExp());
        logger.info("Finished refreshing tokens");
        return refreshedTokenSet;
    }
}
