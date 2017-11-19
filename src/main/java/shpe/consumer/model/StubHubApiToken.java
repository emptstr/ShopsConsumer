package shpe.consumer.model;

import lombok.EqualsAndHashCode;
import org.joda.time.LocalDateTime;

import java.io.Serializable;

/**
 * Created by jordan on 6/15/17.
 */
@EqualsAndHashCode
public  class StubHubApiToken implements Serializable {

    private final String tokenString;
    private final LocalDateTime secondsTillTokenExpires;

    public StubHubApiToken(String tokenString, LocalDateTime secondsTillTokenExpires) {
        this.tokenString = tokenString;
        this.secondsTillTokenExpires = secondsTillTokenExpires;
    }

    public String getTokenString() {
        return tokenString;
    }

    public LocalDateTime getSecondsTillTokenExpires() {
        return secondsTillTokenExpires;
    }
}
