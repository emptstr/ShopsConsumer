package TokenManagement;

import App.StubHubApiScope;
import org.joda.time.LocalDateTime;

import java.io.*;

/**
 * Created by jordan on 6/13/17.
 */
public class TokenManagerImpl implements TokenManager {

    private static final String SAND_BOX_ACCESS_TOKEN_PATH = "../../resources.sb_at.ser";
    private static final String SAND_BOX_REFRESH_TOKEN_PATH = "../../resources.sb_rt.ser";
    private static final String PROD_ACCESS_TOKEN_PATH = "../../resources.p_at.ser";
    private static final String PROD_REFRESH_TOKEN_PATH = "../../resources.p_rt.ser";


    private StubHubApiRefreshToken refreshToken;
    private StubHubApiAccessToken accessToken;

    public StubHubApiAccessToken getAccessToken(StubHubApiScope scope) {

        if (accessToken == null) {
            try {
                File tokenFile;

                if (!scope.equals(StubHubApiScope.PROD)) {
                    tokenFile = new File(SAND_BOX_ACCESS_TOKEN_PATH);

                } else {
                    tokenFile = new File(PROD_ACCESS_TOKEN_PATH);
                }

                if (!tokenFile.exists()) {
                    return null;
                }

                ObjectInputStream objectIn = new ObjectInputStream(new FileInputStream(tokenFile));
                this.accessToken = (StubHubApiAccessToken) objectIn.readObject();

            } catch (Exception e) {
                throw new RuntimeException("An error occured while reading the access token file", e);
            }

        }
        return accessToken;
    }

    public boolean isExpired(StubHubApiAccessToken accessToken) {
        return accessToken.getSecondsToExpr().compareTo(LocalDateTime.now().plusDays(1)) <= 0 ;
    }

    public StubHubApiAccessToken createToken(String accessTokenString, String refreshTokenString, int secondsTillTokenExp, StubHubApiScope scope) {

        try {

            StubHubApiAccessToken accessToken = new StubHubApiAccessToken(accessTokenString, LocalDateTime.now().plusSeconds(secondsTillTokenExp), scope);
            StubHubApiRefreshToken refreshToken = new StubHubApiRefreshToken(refreshTokenString, scope);

            File accessTokenFile, refreshTokenFile;

            if (scope.equals(StubHubApiScope.PROD)) {
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

            return accessToken;
        }catch (IOException e){
            throw new RuntimeException("An error occurred while saving the created tokens",e);
        }
    }

    public StubHubApiRefreshToken getRefreshToken(StubHubApiScope scope) {

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
                throw new RuntimeException("An error occured while reading the access token file", e);
            }
        }
        return refreshToken;
    }

}
