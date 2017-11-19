package shpe.consumer.app;

import shpe.consumer.app.ConsumerRunnable;
import shpe.consumer.controller.ActiveEventUpdateController;
import shpe.consumer.model.StubHubApiToken;
import shpe.consumer.model.TokenSet;
import shpe.consumer.controller.TokenSetController;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConsumerRunnableTest {

    private static final String ACCESS_TOKEN_STRING = "ACCESS_TOKEN_STRING";
    private static final String REFRESH_TOKEN_STRING = "REFRESH_TOKEN_STRING";

    private ConsumerRunnable toolRunner;

    @Mock
    private TokenSetController accessTokenController;
    @Mock
    private ActiveEventUpdateController activeRecordUpdateManager;

    private StubHubApiToken accessToken, refreshToken;
    private TokenSet tokenSet;


    @Before
    public void setUp() {
        toolRunner = new ConsumerRunnable(accessTokenController, activeRecordUpdateManager);

        accessToken = new StubHubApiToken(ACCESS_TOKEN_STRING, LocalDateTime.now());
        refreshToken = new StubHubApiToken(REFRESH_TOKEN_STRING, LocalDateTime.now());
        tokenSet = new TokenSet(accessToken, refreshToken);
    }

    @Test
    /**
     * testRunValidToken
     * - tests the logic when a valid access token is returned
     */
    public void testRunValidToken() {
        when(accessTokenController.retrieveTokenSet()).thenReturn(tokenSet);
        testWorkflow(accessToken);
    }

    private void testWorkflow(final StubHubApiToken accessToken) {
        toolRunner.run();
        verify(activeRecordUpdateManager).update(accessToken);
    }
}