package TokenManagement;

import App.StubHubApiScope;

import java.io.Serializable;

/**
 * Created by jordan on 6/15/17.
 */
public abstract class StubHubApiToken implements Serializable {

    private final String tokenString;
    private final StubHubApiScope scope;


    protected StubHubApiToken(String tokenString, StubHubApiScope scope) {
        this.tokenString = tokenString;
        this.scope = scope;
    }



    public String getTokenString() {
        return tokenString;
    }

    public boolean isProd() {
        return scope.equals(StubHubApiScope.PROD);
    }
}
