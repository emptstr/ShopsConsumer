package shpe.consumer.predicate;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import shpe.consumer.model.StubHubApiToken;
import shpe.consumer.model.TokenSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IsTokenSetValidPredicateTest {

    private IsTokenSetValidPredicate underTest;
    private TokenSet expiredTokenSet;
    private TokenSet nonExpiredTokenSet;

    @Before
    public void setup(){

        StubHubApiToken expiredAccessToken = new StubHubApiToken("accessToken", LocalDateTime.now());
        StubHubApiToken nonexpiredAccessToken = new StubHubApiToken("accessToken2", LocalDateTime.now().plusMonths(6));
        StubHubApiToken refreshToken =  new StubHubApiToken("refreshToken", LocalDateTime.now());
        expiredTokenSet = new TokenSet(expiredAccessToken, refreshToken);
        nonExpiredTokenSet = new TokenSet(nonexpiredAccessToken, refreshToken);
        underTest = new IsTokenSetValidPredicate();
    }

    @Test
    public void shouldReturnFalse() throws Exception {
        assertFalse(underTest.test(expiredTokenSet));
    }

    @Test
    public void shouldReturnTrue() throws Exception {
        assertTrue(underTest.test(nonExpiredTokenSet));
    }

}