package shpe.consumer.generator;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shpe.consumer.accessor.LoginApiAccessor;
import shpe.consumer.controller.TokenSetFactory;
import shpe.consumer.model.StubHubApiLoginResult;
import shpe.consumer.model.TokenSet;
@RequiredArgsConstructor
public class TokenSetGeneratorImpl extends TokenSetGenerator {

    private static final Logger logger = LoggerFactory.getLogger(TokenSetGeneratorImpl.class);

    private final TokenSetFactory tokenSetFactory;
    private final LoginApiAccessor loginManager;

    //TODO integration test

    @Override
    public TokenSet generateNewTokenSet() {
        logger.info("creating tokens");
        StubHubApiLoginResult loginResult = loginManager.login();
        TokenSet generatedTokenSet = tokenSetFactory.createTokens(loginResult.getAccessTokenString(),
                loginResult.getRefreshTokenString(), loginResult.getSecondsToExp());
        logger.info("Finished creating tokens");
        return  generatedTokenSet;
    }
}
