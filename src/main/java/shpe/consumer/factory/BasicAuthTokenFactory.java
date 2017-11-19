package shpe.consumer.factory;

/**
 * Created by jordan on 6/14/17.
 */
public interface BasicAuthTokenFactory {

    public String generate(String consumerKey, String consumerSecret);
}
