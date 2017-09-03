package shpe.consumer.controller;

import lombok.RequiredArgsConstructor;
import shpe.consumer.dao.TokenSetDao;
import shpe.consumer.generator.TokenSetGenerator;
import shpe.consumer.model.TokenSet;
import shpe.consumer.predicate.IsTokenSetValidPredicate;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class TokenSetControllerImpl extends TokenSetController {

    private static final Logger logger = Logger.getLogger(TokenSetControllerImpl.class.getName());
    private static final String TOKEN_SET_PATH = "../../resources.token-set.ser";

    private final TokenSetDao tokenDao;
    private final TokenSetRefreshController accessTokenRefreshController;
    private final TokenSetGenerator tokenSetGenerator;
    private final IsTokenSetValidPredicate isTokenSetValidPredicate;

    @Override
    public TokenSet retrieveTokenSet() {
        return retrieveValidTokenSet();
    }

    private TokenSet retrieveValidTokenSet() {
        TokenSet serializedAccessToken = tokenDao.fetchTokenSet(TOKEN_SET_PATH);
        if (serializedAccessToken == null) {
            return createAccessTokenForTheFirstTime();
        } else if (!isTokenSetValidPredicate.test(serializedAccessToken)) {
            serializedAccessToken = refreshExpiredAccessToken(serializedAccessToken);
        }
        return serializedAccessToken;
    }


    private TokenSet refreshExpiredAccessToken(final TokenSet tokenSet) {
        TokenSet accessToken = accessTokenRefreshController.refreshExpiredTokenSet(tokenSet);
        tokenDao.persistTokenSet(TOKEN_SET_PATH, accessToken);
        return accessToken;
    }

    private TokenSet createAccessTokenForTheFirstTime() {
        TokenSet newTokens = tokenSetGenerator.generateNewTokenSet();
        tokenDao.persistTokenSet(TOKEN_SET_PATH, newTokens);
        return newTokens;
    }

//    @Override
//    public boolean hasTokenExpired(final StubHubApiAccessToken accessToken) {
//        return accessToken.getSecondsToExpr().compareTo(LocalDateTime.now().plusDays(1)) <= 0;
//    }
//
//    @Override
//    public StubHubApiAccessToken createTokens(final String accessTokenString, final String refreshTokenString,
//                                              final int secondsTillTokenExp, final StubHubApiScope tokenScope) {
//        logger.log(Level.INFO, "Started creating access tokens with scope: " + tokenScope.toString());
//        try {
//
//            StubHubApiAccessToken accessToken = new StubHubApiAccessToken(accessTokenString, LocalDateTime.now().plusSeconds(secondsTillTokenExp), tokenScope);
//            StubHubApiRefreshToken refreshToken = new StubHubApiRefreshToken(refreshTokenString, tokenScope);
//
//            File accessTokenFile, refreshTokenFile;
//
//            if (tokenScope.equals(StubHubApiScope.PROD)) {
//                accessTokenFile = new File(TOKEN_SET_PATH);
//                refreshTokenFile = new File(PROD_REFRESH_TOKEN_PATH);
//            } else {
//                accessTokenFile = new File(SAND_BOX_ACCESS_TOKEN_PATH);
//                refreshTokenFile = new File(SAND_BOX_REFRESH_TOKEN_PATH);
//            }
//
//            ObjectOutputStream accessOut = new ObjectOutputStream(new FileOutputStream(accessTokenFile));
//            ObjectOutputStream refreshOut = new ObjectOutputStream(new FileOutputStream(refreshTokenFile));
//
//            accessOut.writeObject(accessToken);
//            refreshOut.writeObject(refreshToken);
//
//            this.accessToken = accessToken;
//            this.refreshToken = refreshToken;
//            logger.log(Level.INFO, "Finished creating tokens with scope: " + tokenScope.toString());
//            return accessToken;
//        } catch (IOException e) {
//            throw new RuntimeException("Failed while saving the created tokens", e);
//        }
//    }
//
//    @Override
//    public StubHubApiRefreshToken getRefreshToken(final StubHubApiScope scope) {
//        logger.log(Level.INFO, "Started retrieving access token with scope: " + scope.toString());
//        if (refreshToken == null) {
//            try {
//                File tokenFile;
//
//                if (!scope.equals(StubHubApiScope.PROD)) {
//                    tokenFile = new File(SAND_BOX_REFRESH_TOKEN_PATH);
//
//                } else {
//                    tokenFile = new File(PROD_REFRESH_TOKEN_PATH);
//                }
//
//                if (!tokenFile.exists()) {
//                    return null;
//                }
//
//                ObjectInputStream objectIn = new ObjectInputStream(new FileInputStream(tokenFile));
//                this.refreshToken = (StubHubApiRefreshToken) objectIn.readObject();
//
//            } catch (Exception e) {
//                throw new RuntimeException("An error occurred while reading the access token file", e);
//            }
//        }
//        logger.log(Level.INFO, "Finished retrieving refresh token with scope: " + scope.toString());
//        return refreshToken;
//    }

}
