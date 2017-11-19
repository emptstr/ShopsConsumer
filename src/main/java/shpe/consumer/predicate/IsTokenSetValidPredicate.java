package shpe.consumer.predicate;

import org.joda.time.LocalDateTime;
import shpe.consumer.model.StubHubApiToken;
import shpe.consumer.model.TokenSet;
import java.util.function.Predicate;

public class IsTokenSetValidPredicate implements Predicate<TokenSet> {
    @Override
    public boolean test(TokenSet tokenSet) {
        StubHubApiToken accessToken = tokenSet.getAccessToken();
        return accessToken.getSecondsTillTokenExpires().compareTo(LocalDateTime.now().plusDays(1)) > 0;
    }
}
