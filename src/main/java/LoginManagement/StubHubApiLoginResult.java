package LoginManagement;

/**
 * Created by jordan on 6/13/17.
 */
public class StubHubApiLoginResult {

    private final String accessTokenString;
    private final String refreshTokenString;
    private final int secondsToExp;

    public StubHubApiLoginResult(String accessTokenString, String refreshTokenString, int secondsToExp) {
        this.accessTokenString = accessTokenString;
        this.refreshTokenString = refreshTokenString;
        this.secondsToExp = secondsToExp;
    }

    public String getAccessTokenString() {
        return accessTokenString;
    }

    public String getRefreshTokenString() {
        return refreshTokenString;
    }

    public int getSecondsToExp() {
        return secondsToExp;
    }
}

