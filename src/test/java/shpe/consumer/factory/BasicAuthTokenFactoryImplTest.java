package shpe.consumer.factory;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import shpe.consumer.factory.BasicAuthTokenFactoryImpl;


import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by jordan on 6/14/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class BasicAuthTokenFactoryImplTest {

    private static final String COLON_STRING = "%s:%s";
    private static final String EXPECTED_AUTH_TOKEN = "TOKEN";
    private static final String CONSUMER_KEY = "CONSUMER_KEY";
    private static final String CONSUMER_SECRET = "CONSUMER_SECRET";

    @Mock
    private Base64 base64;
    private BasicAuthTokenFactoryImpl tokenGenerator;

    @Test
    public void testGenerateToken() {
        tokenGenerator = new BasicAuthTokenFactoryImpl(base64);
        when(base64.encodeToString(String.format(COLON_STRING, CONSUMER_KEY, CONSUMER_SECRET).getBytes())).thenReturn(EXPECTED_AUTH_TOKEN);
        String basicAuthToken = tokenGenerator.generate(CONSUMER_KEY, CONSUMER_SECRET);
        assertEquals(basicAuthToken, EXPECTED_AUTH_TOKEN);
    }

    @Test
    public void authTokenTest(){
    }

}