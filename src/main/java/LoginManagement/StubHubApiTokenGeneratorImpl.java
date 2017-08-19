package LoginManagement;


import org.apache.commons.codec.binary.Base64;

/**
 * Created by jordan on 6/14/17.
 */
public class StubHubApiTokenGeneratorImpl implements StubHubApiTokenGenerator {

    private static final String COLON_STRING = "%s:%s";
    private final Base64 base64;

    public StubHubApiTokenGeneratorImpl(Base64 base64) {
        this.base64 = base64;
    }

    public String generate(String consumerKey, String consumerSecret) {
        return base64.encodeToString(String.format(COLON_STRING, consumerKey, consumerSecret).getBytes());
    }
}
