package CredentialManagement;

import App.StubHubApiScope;
import CredentialManagement.StubHubApiCredentials;
import CredentialManagement.StubHubApiCredentialsManagerImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class StubHubApiCredentialsManagerImplTest {

    private static final String SAND_CONSUMER_KEY_STR = "SandConsumerKey";
    private static final String SAND_SECRET_KEY_STR = "SandSecretKey";
    private static final String SAND_USER_STR = "SandUser";
    private static final String SAND_PASS_STR = "SandPass";
    private static final String PROD_CONSUMER_KEY_STR = "ProdConsumerKey";
    private static final String PROD_SECRET_KEY_STR = "ProdSecretKey";
    private static final String PROD_USER_STR = "ProdUser";
    private static final String PROD_PASS_STR = "ProdPass";

    private static final File propertiesFile = new File("config.properties");

    private StubHubApiCredentialsManagerImpl credentialsManager;
    private Properties properties = new Properties();
    private StubHubApiCredentials credentials;

    @Before
    public void setUp() throws IOException {
        credentialsManager = new StubHubApiCredentialsManagerImpl(properties);
        properties.load(new FileInputStream(propertiesFile));
    }

    @Test
    public void testGetCredentialsProd() {
        String consumerKey = properties.getProperty(PROD_CONSUMER_KEY_STR);
        String consumerSecret = properties.getProperty(PROD_SECRET_KEY_STR);
        String username = properties.getProperty(PROD_USER_STR);
        String password = properties.getProperty(PROD_PASS_STR);

        credentials = new StubHubApiCredentials(consumerKey, consumerSecret, username, password, StubHubApiScope.PROD);
        StubHubApiCredentials credentials = credentialsManager.getCredentials(StubHubApiScope.PROD);

        assertEquals(credentials.getConsumerKey(), consumerKey);
        assertEquals(credentials.getConsumerSecret(), consumerSecret);
        assertEquals(credentials.getUsername(), username);
        assertEquals(credentials.getPassword(), password);
        assertEquals(credentials.getScope(), StubHubApiScope.PROD);
    }

    @Test
    public void testGetCredentialsSand() {
        String consumerKey = properties.getProperty(SAND_CONSUMER_KEY_STR);
        String consumerSecret = properties.getProperty(SAND_SECRET_KEY_STR);
        String username = properties.getProperty(SAND_USER_STR);
        String password = properties.getProperty(SAND_PASS_STR);

        credentials = new StubHubApiCredentials(consumerKey, consumerSecret, username, password, StubHubApiScope.SANDBOX);
        StubHubApiCredentials credentials = credentialsManager.getCredentials(StubHubApiScope.SANDBOX);

        assertEquals(credentials.getConsumerKey(), consumerKey);
        assertEquals(credentials.getConsumerSecret(), consumerSecret);
        assertEquals(credentials.getUsername(), username);
        assertEquals(credentials.getPassword(), password);
        assertEquals(credentials.getScope(), StubHubApiScope.SANDBOX);
    }
}