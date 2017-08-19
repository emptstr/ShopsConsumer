package CredentialManagement;

import App.StubHubApiScope;
import com.google.inject.Inject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;


public class StubHubApiCredentialsManagerImpl extends StubHubApiCredentialsManager {

    private static final Logger logger = Logger.getLogger(StubHubApiCredentialsManagerImpl.class.getName());

    private static final String SAND_CONSUMER_KEY_STR = "SandConsumerKey";
    private static final String SAND_SECRET_KEY_STR = "SandSecretKey";
    private static final String SAND_USER_STR = "SandUser";
    private static final String SAND_PASS_STR = "SandPass";
    private static final String PROD_CONSUMER_KEY_STR = "ProdConsumerKey";
    private static final String PROD_SECRET_KEY_STR = "ProdSecretKey";
    private static final String PROD_USER_STR = "ProdUser";
    private static final String PROD_PASS_STR = "ProdPass";

    private static final File PROPERTIES_FILE = new File("config.properties");

    private final Properties properties;
    private StubHubApiCredentials credentials;

    @Inject
    public StubHubApiCredentialsManagerImpl(final Properties properties) {
        this.properties = properties;
        this.credentials = null;
    }

    public StubHubApiCredentials getCredentials(StubHubApiScope scope) {

        if (this.credentials == null) {

            try {
                properties.load(new FileInputStream(PROPERTIES_FILE));

                String consumerKey;
                String secretKey;
                String username;
                String password;

                if (scope.equals(StubHubApiScope.PROD)) {
                    consumerKey = properties.getProperty(PROD_CONSUMER_KEY_STR);
                    secretKey = properties.getProperty(PROD_SECRET_KEY_STR);
                    username = properties.getProperty(PROD_USER_STR);
                    password = properties.getProperty(PROD_PASS_STR);
                } else {
                    consumerKey = properties.getProperty(SAND_CONSUMER_KEY_STR);
                    secretKey = properties.getProperty(SAND_SECRET_KEY_STR);
                    username = properties.getProperty(SAND_USER_STR);
                    password = properties.getProperty(SAND_PASS_STR);
                }

                this.credentials = new StubHubApiCredentials(consumerKey, secretKey, username, password, scope);
            } catch (IOException e) {
                throw new RuntimeException("Error while loading properties file", e);
            }
        }
        return this.credentials;
    }
}
