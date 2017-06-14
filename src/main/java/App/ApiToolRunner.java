package App;

import CredentialManagement.StubHubApiCredentials;
import CredentialManagement.StubHubApiCredentialsManager;
import EventManagement.EventUpdateManager;
import EventManagement.StubHubEvent;
import ListingManagement.ListingUpdateManager;
import LoginManagement.LoginManager;
import LoginManagement.StubHubApiLoginResult;
import TokenManagement.StubHubApiAccessToken;
import TokenManagement.TokenManager;
import com.google.inject.Inject;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ApiToolRunner
 */
public class ApiToolRunner implements Runnable {

    private static final Logger logger = Logger.getLogger(ApiToolRunner.class.getName());
    private static final StubHubApiScope APP_SCOPE = StubHubApiScope.PROD;

    private final TokenManager tokenManager;
    private final StubHubApiCredentialsManager credentialsManager;
    private final LoginManager loginManager;
    private final EventUpdateManager eventManager;
    private final ListingUpdateManager listingManager;

    @Inject
    public ApiToolRunner(final TokenManager tokenManager, final StubHubApiCredentialsManager credentialsManager, final LoginManager loginManager, final EventUpdateManager eventManager, final ListingUpdateManager listingManager) {
        this.tokenManager = tokenManager;
        this.credentialsManager = credentialsManager;
        this.loginManager = loginManager;
        this.eventManager = eventManager;
        this.listingManager = listingManager;
    }

    //TODO comment
    public void run() {
        logger.log(Level.INFO, "Started api tool runner");
        try {
            StubHubApiAccessToken accessToken = tokenManager.getAccessToken(APP_SCOPE);

            if (accessToken != null) {
                if (tokenManager.isExpired(accessToken)) {
                    logger.log(Level.INFO, "Access token has expired");
                    StubHubApiCredentials credentials = credentialsManager.getCredentials(APP_SCOPE);
                    StubHubApiLoginResult refreshResult = loginManager.refresh(credentials, tokenManager.getRefreshToken(APP_SCOPE));
                    accessToken = tokenManager.createToken(refreshResult.getAccessTokenString(), refreshResult.getRefreshTokenString(), refreshResult.getSecondsToExp(), APP_SCOPE);
                    logger.log(Level.INFO, "Finished refreshing tokens");
                }
            } else {
                logger.log(Level.INFO, "Access token does not exist");
                StubHubApiCredentials credentials = credentialsManager.getCredentials(APP_SCOPE);
                StubHubApiLoginResult loginResult = loginManager.login(credentials);
                accessToken = tokenManager.createToken(loginResult.getAccessTokenString(), loginResult.getRefreshTokenString(), loginResult.getSecondsToExp(), APP_SCOPE);
                logger.log(Level.INFO, "Finished creating tokens");
            }
            List<StubHubEvent> events = eventManager.update(accessToken);
            listingManager.update(accessToken, events);
            logger.log(Level.INFO, "Api Tool Runner finished");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
