package shpe.consumer.dao;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import shpe.consumer.model.StubHubApiToken;
import shpe.consumer.model.TokenSet;

import java.io.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by jordan on 9/7/17.
 */
public class TokenSetDaoImplTest {

    private TokenSetDaoImpl tokenSetDao;
    private String tokenSetKey = "src/main/resources/token-set.ser";
    private TokenSet tokenToBeSaved = new TokenSet(new StubHubApiToken("accessToken", LocalDateTime.now()),
            new StubHubApiToken("refreshToken", LocalDateTime.now()));

    @Before
    public void setup(){
        File tokenSetFile = new File(tokenSetKey);
        if(tokenSetFile.exists()){
            tokenSetFile.delete();
        }
        tokenSetDao = new TokenSetDaoImpl();
    }

    @Test
    public void  shouldSerializeTokenSet() throws Exception {


        tokenSetDao.persistTokenSet(tokenSetKey, tokenToBeSaved);

        File tokenSetFile = new File(tokenSetKey);
        ObjectInputStream tokenSetStream = new ObjectInputStream(new FileInputStream(tokenSetFile));
        TokenSet actualResult = (TokenSet) tokenSetStream.readObject();

        assertEquals(tokenToBeSaved, actualResult);
    }

    @Test
    public void shouldDeserializeTokenSet()throws Exception {
        File tokenSetFile = new File(tokenSetKey);
        ObjectOutputStream tokenSetStream = new ObjectOutputStream(new FileOutputStream(tokenSetFile));
        tokenSetStream.writeObject(tokenToBeSaved);

        TokenSet actualTokenSet = tokenSetDao.fetchTokenSet(tokenSetKey);

        assertEquals(tokenToBeSaved, actualTokenSet);
    }

}