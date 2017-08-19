package TokenManagement;

import App.StubHubApiScope;
import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import static org.junit.Assert.*;

/**
 * @author Jordan Gaston
 * @version 0.1.17
 */
@RunWith(MockitoJUnitRunner.class)
public class TokenManagerImplTest {

    private static final String SAND_BOX_ACCESS_TOKEN_PATH = "../../resources.sb_at.ser";
    private static final String SAND_BOX_REFRESH_TOKEN_PATH = "../../resources.sb_rt.ser";
    private static final String PROD_ACCESS_TOKEN_PATH = "../../resources.p_at.ser";
    private static final String PROD_REFRESH_TOKEN_PATH = "../../resources.p_rt.ser";

    private static final String ACCESS_TOKEN_STRING = "ACCESS_TOKEN";
    private static final String REFRESH_TOKEN_STRING = "REFRESH_TOKEN";

    private TokenManager tokenManager;
    private LocalDateTime accessTokenTime;
    private LocalDateTime refreshTokenTime;


    @Before
    public void setUp() {
        tokenManager = new TokenManagerImpl();
    }

    @Test
    public void testGetAccessTokenProd() throws IOException {
        accessTokenTime = LocalDateTime.now();
        StubHubApiAccessToken accessToken = new StubHubApiAccessToken(ACCESS_TOKEN_STRING, accessTokenTime, StubHubApiScope.PROD);

        File accessTokenFile = new File(PROD_ACCESS_TOKEN_PATH);
        FileOutputStream outputStream = new FileOutputStream(accessTokenFile);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(accessToken);

        StubHubApiAccessToken result = tokenManager.getAccessToken(StubHubApiScope.PROD);

        assertEquals(result.getTokenString(), accessToken.getTokenString());
        assertEquals(result.getSecondsToExpr(), accessToken.getSecondsToExpr());
        assertEquals(result.isProd(), accessToken.isProd());
    }

    @Test
    public void testGetAccessTokenSand() throws IOException {
        accessTokenTime = LocalDateTime.now();
        StubHubApiAccessToken accessToken = new StubHubApiAccessToken(ACCESS_TOKEN_STRING, accessTokenTime, StubHubApiScope.SANDBOX);

        File accessTokenFile = new File(SAND_BOX_ACCESS_TOKEN_PATH);
        FileOutputStream outputStream = new FileOutputStream(accessTokenFile);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(accessToken);

        StubHubApiAccessToken result = tokenManager.getAccessToken(StubHubApiScope.SANDBOX);

        assertEquals(result.getTokenString(), accessToken.getTokenString());
        assertEquals(result.getSecondsToExpr(), accessToken.getSecondsToExpr());
        assertEquals(result.isProd(), accessToken.isProd());
    }

    @Test
    public void testGetAccessTokenNull() throws IOException {
        File accessTokenFile = new File(SAND_BOX_ACCESS_TOKEN_PATH);
        if (accessTokenFile.exists()) {
            accessTokenFile.delete();
        }
        assertNull(tokenManager.getAccessToken(StubHubApiScope.SANDBOX));

         accessTokenFile = new File(PROD_ACCESS_TOKEN_PATH);
        if (accessTokenFile.exists()) {
            accessTokenFile.delete();
        }
        assertNull(tokenManager.getAccessToken(StubHubApiScope.PROD));
    }

    @Test
    public void getRefreshTokenProd() throws IOException {
        refreshTokenTime = LocalDateTime.now();
        StubHubApiRefreshToken refreshToken = new StubHubApiRefreshToken(REFRESH_TOKEN_STRING,StubHubApiScope.PROD);

        File refreshTokenFile = new File(PROD_REFRESH_TOKEN_PATH);
        FileOutputStream outputStream = new FileOutputStream(refreshTokenFile);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(refreshToken);

        StubHubApiRefreshToken result = tokenManager.getRefreshToken(StubHubApiScope.PROD);

        assertEquals(result.getTokenString(), refreshToken.getTokenString());
        assertEquals(result.isProd(), refreshToken.isProd());
    }


    @Test
    public void getRefreshTokenSand() throws IOException {
        refreshTokenTime = LocalDateTime.now();
        StubHubApiRefreshToken refreshToken = new StubHubApiRefreshToken(REFRESH_TOKEN_STRING,StubHubApiScope.SANDBOX);

        File refreshTokenFile = new File(SAND_BOX_REFRESH_TOKEN_PATH);
        FileOutputStream outputStream = new FileOutputStream(refreshTokenFile);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(refreshToken);

        StubHubApiRefreshToken result = tokenManager.getRefreshToken(StubHubApiScope.SANDBOX);

        assertEquals(result.getTokenString(), refreshToken.getTokenString());
        assertEquals(result.isProd(), refreshToken.isProd());
    }

    @Test
    public void testGetRefreshTokenNull() throws IOException {
        File refreshTokenFile = new File(SAND_BOX_REFRESH_TOKEN_PATH);
        if (refreshTokenFile.exists()) {
            refreshTokenFile.delete();
        }
        assertNull(tokenManager.getRefreshToken(StubHubApiScope.SANDBOX));

        refreshTokenFile = new File(PROD_REFRESH_TOKEN_PATH);
        if (refreshTokenFile.exists()) {
            refreshTokenFile.delete();
        }
        assertNull(tokenManager.getRefreshToken(StubHubApiScope.PROD));
    }

    @Test
    public void testIsExpired() {
        accessTokenTime = LocalDateTime.now();
        StubHubApiAccessToken accessToken = new StubHubApiAccessToken(ACCESS_TOKEN_STRING, accessTokenTime, StubHubApiScope.PROD );
        assertTrue(tokenManager.isExpired(accessToken));

        accessTokenTime = LocalDateTime.now().plusWeeks(2);
        accessToken = new StubHubApiAccessToken(ACCESS_TOKEN_STRING, accessTokenTime, StubHubApiScope.PROD);
        assertFalse(tokenManager.isExpired(accessToken));
    }

    @Test
    public void testCreateToken() {

        DateTimeUtils.setCurrentMillisFixed(1000);

        int seconds_to_exr = 1234;
        accessTokenTime = LocalDateTime.now().plusSeconds(seconds_to_exr);

        StubHubApiAccessToken accessToken = new StubHubApiAccessToken(ACCESS_TOKEN_STRING, accessTokenTime, StubHubApiScope.PROD );
        StubHubApiRefreshToken refreshToken = new StubHubApiRefreshToken(REFRESH_TOKEN_STRING, StubHubApiScope.PROD);

        StubHubApiAccessToken resultAccessToken = tokenManager.createTokens(ACCESS_TOKEN_STRING, REFRESH_TOKEN_STRING, seconds_to_exr, StubHubApiScope.PROD);
        StubHubApiRefreshToken resultRefreshToken = tokenManager.getRefreshToken(StubHubApiScope.PROD);

        assertEquals(resultRefreshToken.getTokenString(), refreshToken.getTokenString());
        assertEquals(resultRefreshToken.isProd(), refreshToken.isProd());

        assertEquals(resultAccessToken.getTokenString(), accessToken.getTokenString());
        assertEquals(resultAccessToken.getSecondsToExpr(), accessToken.getSecondsToExpr());
        assertEquals(resultAccessToken.isProd(), accessToken.isProd());

        DateTimeUtils.setCurrentMillisSystem();
    }
}