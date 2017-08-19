package App;

import CredentialManagement.StubHubApiCredentials;
import CredentialManagement.StubHubApiCredentialsManager;
import LoginManagement.StubHubApiLoginManager;
import LoginManagement.StubHubApiLoginResult;
import TokenManagement.StubHubApiAccessToken;
import TokenManagement.TokenManager;
import com.google.inject.Inject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ApiToolRunner
 * @author Jordan Gaston
 * @version 0.1.17
 */
public class ApiToolRunner implements Runnable {

    private static final Logger logger = Logger.getLogger(ApiToolRunner.class.getName());
    private static final StubHubApiScope APP_SCOPE = StubHubApiScope.PROD;

    private final TokenManager tokenManager;
    private final StubHubApiCredentialsManager credentialsManager;
    private final StubHubApiLoginManager loginManager;
    private final StubHubActiveRecordUpdateManager updateManager;

    @Inject
    public ApiToolRunner(final TokenManager tokenManager, final StubHubApiCredentialsManager credentialsManager, final StubHubApiLoginManager loginManager, final StubHubActiveRecordUpdateManager updateManager) {
        this.tokenManager = tokenManager;
        this.credentialsManager = credentialsManager;
        this.loginManager = loginManager;
        this.updateManager = updateManager;
    }

    /**
     * run
     * this method contains the primary application logic
     * retrieves the credentials necessary to invoke the StubHubApi's
     * calls the api and processes the results
     */
    public void run() {
        logger.log(Level.INFO, "Started api tool runner");
        try {
            StubHubApiAccessToken accessToken = tokenManager.getAccessToken(APP_SCOPE);

            if (accessToken != null) {
                if (tokenManager.isExpired(accessToken)) {
                    logger.log(Level.INFO, "Access token has expired");
                    StubHubApiCredentials credentials = credentialsManager.getCredentials(APP_SCOPE);
                    StubHubApiLoginResult refreshResult = loginManager.refresh(credentials, tokenManager.getRefreshToken(APP_SCOPE));
                    accessToken = tokenManager.createTokens(refreshResult.getAccessTokenString(), refreshResult.getRefreshTokenString(), refreshResult.getSecondsToExp(), APP_SCOPE);
                    logger.log(Level.INFO, "Finished refreshing tokens");
                }
            } else {
                logger.log(Level.INFO, "Access token does not exist");
                StubHubApiCredentials credentials = credentialsManager.getCredentials(APP_SCOPE);
                StubHubApiLoginResult loginResult = loginManager.login(credentials);
                accessToken = tokenManager.createTokens(loginResult.getAccessTokenString(), loginResult.getRefreshTokenString(), loginResult.getSecondsToExp(), APP_SCOPE);
                logger.log(Level.INFO, "Finished creating tokens");
            }
            updateManager.update(accessToken);
            logger.log(Level.INFO, "Api Tool Runner finished");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
