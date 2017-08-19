package TokenManagement;

import App.StubHubApiScope;
import org.joda.time.LocalDateTime;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TokenManagerImpl
 *
 * @author Jordan Gaston
 * @version 0.1.17
 */
public class TokenManagerImpl extends TokenManager {

    private static final Logger logger = Logger.getLogger(TokenManagerImpl.class.getName());

    private static final String SAND_BOX_ACCESS_TOKEN_PATH = "../../resources.sb_at.ser";
    private static final String SAND_BOX_REFRESH_TOKEN_PATH = "../../resources.sb_rt.ser";
    private static final String PROD_ACCESS_TOKEN_PATH = "../../resources.p_at.ser";
    private static final String PROD_REFRESH_TOKEN_PATH = "../../resources.p_rt.ser";


    private StubHubApiRefreshToken refreshToken;
    private StubHubApiAccessToken accessToken;

    @Override
    public StubHubApiAccessToken getAccessToken(final StubHubApiScope scope) {
        logger.log(Level.INFO, "Started retrieving access token with scope: " + scope.toString());
        if (accessToken == null) {
            try {
                File tokenFile;

                if (!scope.equals(StubHubApiScope.PROD)) {
                    tokenFile = new File(SAND_BOX_ACCESS_TOKEN_PATH);

                } else {
                    tokenFile = new File(PROD_ACCESS_TOKEN_PATH);
                }

                if (!tokenFile.exists()) {
                    logger.log(Level.INFO, "No existing access token");
                    return null;
                }

                ObjectInputStream objectIn = new ObjectInputStream(new FileInputStream(tokenFile));
                this.accessToken = (StubHubApiAccessToken) objectIn.readObject();

            } catch (Exception e) {
                throw new RuntimeException("Failed while reading the access token file", e);
            }

        }
        logger.log(Level.INFO, "Finished retrieving access token with scope: " + scope.toString());
        return accessToken;
    }

    @Override
    public boolean isExpired(final StubHubApiAccessToken accessToken) {
        return accessToken.getSecondsToExpr().compareTo(LocalDateTime.now().plusDays(1)) <= 0;
    }

    @Override
    public StubHubApiAccessToken createTokens(final String accessTokenString, final String refreshTokenString,
                                              final int secondsTillTokenExp, final StubHubApiScope tokenScope) {
        logger.log(Level.INFO, "Started creating access tokens with scope: " + tokenScope.toString());
        try {

            StubHubApiAccessToken accessToken = new StubHubApiAccessToken(accessTokenString, LocalDateTime.now().plusSeconds(secondsTillTokenExp), tokenScope);
            StubHubApiRefreshToken refreshToken = new StubHubApiRefreshToken(refreshTokenString, tokenScope);

            File accessTokenFile, refreshTokenFile;

            if (tokenScope.equals(StubHubApiScope.PROD)) {
                accessTokenFile = new File(PROD_ACCESS_TOKEN_PATH);
                refreshTokenFile = new File(PROD_REFRESH_TOKEN_PATH);
            } else {
                accessTokenFile = new File(SAND_BOX_ACCESS_TOKEN_PATH);
                refreshTokenFile = new File(SAND_BOX_REFRESH_TOKEN_PATH);
            }

            ObjectOutputStream accessOut = new ObjectOutputStream(new FileOutputStream(accessTokenFile));
            ObjectOutputStream refreshOut = new ObjectOutputStream(new FileOutputStream(refreshTokenFile));

            accessOut.writeObject(accessToken);
            refreshOut.writeObject(refreshToken);

            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            logger.log(Level.INFO, "Finished creating tokens with scope: " + tokenScope.toString());
            return accessToken;
        } catch (IOException e) {
            throw new RuntimeException("Failed while saving the created tokens", e);
        }
    }

    @Override
    public StubHubApiRefreshToken getRefreshToken(final StubHubApiScope scope) {
        logger.log(Level.INFO, "Started retrieving access token with scope: " + scope.toString());
        if (refreshToken == null) {
            try {
                File tokenFile;

                if (!scope.equals(StubHubApiScope.PROD)) {
                    tokenFile = new File(SAND_BOX_REFRESH_TOKEN_PATH);

                } else {
                    tokenFile = new File(PROD_REFRESH_TOKEN_PATH);
                }

                if (!tokenFile.exists()) {
                    return null;
                }

                ObjectInputStream objectIn = new ObjectInputStream(new FileInputStream(tokenFile));
                this.refreshToken = (StubHubApiRefreshToken) objectIn.readObject();

            } catch (Exception e) {
                throw new RuntimeException("An error occurred while reading the access token file", e);
            }
        }
        logger.log(Level.INFO, "Finished retrieving refresh token with scope: " + scope.toString());
        return refreshToken;
    }

}
