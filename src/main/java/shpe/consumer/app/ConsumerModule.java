package shpe.consumer.app;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.codahale.metrics.MetricRegistry;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.apache.commons.codec.binary.Base64;
import shpe.consumer.accessor.*;
import shpe.consumer.controller.*;
import shpe.consumer.dao.AttributeUpdateFactory;
import shpe.consumer.dao.StubHubDynamoDbEventDao;
import shpe.consumer.dao.StubHubEventDao;
import shpe.consumer.dao.TokenSetDaoImpl;
import shpe.consumer.deserializer.StubHubEventDeserializer;
import shpe.consumer.deserializer.StubHubListingDeserializer;
import shpe.consumer.factory.BasicAuthTokenFactoryImpl;
import shpe.consumer.factory.StringEntityFactory;
import shpe.consumer.generator.TokenSetGenerator;
import shpe.consumer.generator.TokenSetGeneratorImpl;
import shpe.consumer.model.StubHubApiCredentials;
import shpe.consumer.predicate.IsTokenSetValidPredicate;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Jordan Gaston
 * @version 0.1.17
 */
public class ConsumerModule extends AbstractModule {


    private final Properties properties = new Properties();

    protected void configure() {
        try {
            properties.load(new FileReader("config.properties"));
            Names.bindProperties(binder(), properties);
        } catch (IOException ex) {
            //...
        }
        bind(ConsumerRunnable.class).to(ConsumerRunnable.class);
    }


    @Provides
    @Singleton
    public ConsumerRunnable provideConsumerRunnable(TokenSetController tokenSetController, ActiveEventUpdateController activeEventUpdateController) {
        return new ConsumerRunnable(tokenSetController, activeEventUpdateController);
    }

    @Provides
    @Singleton
    public TokenSetController provideTokenSetController(TokenSetRefreshController tokenSetRefreshController,
                                                        TokenSetGenerator tokenSetGenerator) {
        return new TokenSetControllerImpl(new TokenSetDaoImpl(), tokenSetRefreshController, tokenSetGenerator, new IsTokenSetValidPredicate());
    }

    @Provides
    @Singleton
    public TokenSetRefreshController provideTokenRefreshController(LoginApiAccessor loginApiAccessor) {
        return new TokenSetRefreshControllerImpl(new TokenSetFactory(), loginApiAccessor);
    }

    @Provides
    @Singleton
    public TokenSetGenerator provideTokenSetGenerator(LoginApiAccessor loginApiAccessor) {
        return new TokenSetGeneratorImpl(new TokenSetFactory(), loginApiAccessor);
    }

    @Provides
    @Singleton
    public LoginApiAccessor provideLoginApiAccessor(StubHubApiCredentials credentials, Client client) {
        return new LoginApiAccessorImpl(credentials, client, new BasicAuthTokenFactoryImpl(new Base64()), new StringEntityFactory());
    }

    @Provides
    @Singleton
    public StubHubApiCredentials provideCredentials(String consumerSecret, String username,
                                                    String password) {
        return new StubHubApiCredentials(properties.getProperty("CONSUMER_KEY"), properties.getProperty("CONSUMER_SECRET"),
                properties.getProperty("USERNAME"), properties.getProperty("PASSWORD"));
    }

    @Provides
    @Singleton
    public Client provideClient() {
        return ClientBuilder.newClient();
    }

    @Provides
    @Singleton
    public ActiveEventUpdateController provideActiveEventUpdateController(EventUpdateController eventUpdateController
            , EventListingUpdateController eventListingUpdateController, StubHubEventDao eventDao, MetricRegistry metricRegistry) {
        return new ActiveEventUpdateControllerImpl(eventUpdateController, eventListingUpdateController, eventDao, metricRegistry);
    }

    @Provides
    @Singleton
    public EventUpdateController provideEventUpdateController(EventApiAccessor eventRetriever, MetricRegistry metricRegistry) {
        return new EventUpdateControllerImpl(eventRetriever, Integer.parseInt(properties.getProperty("EVENTS_PER_REQUEST_CONSTRAINT"))
                , Integer.parseInt(properties.getProperty("REQUESTS_PER_MINUTE_CONSTRAINT")),
                Integer.parseInt(properties.getProperty("TIMEOUT_DURATION_IN_SECONDS")), metricRegistry);
    }

    @Provides
    @Singleton
    public EventApiAccessor provideEventApiAccessor(Client client){
        return new EventApiAccessorImpl(client, new GsonBuilder(), new StubHubEventDeserializer());
    }

    @Provides
    @Singleton
    public EventListingUpdateController provideEventListingUpdateController(ListingApiAccessor activeListingRetriever, MetricRegistry metricRegistry) {
        return new EventListingUpdateControllerImpl(activeListingRetriever, Integer.parseInt(properties.getProperty("LISTINGS_PER_REQUEST_CONSTRAINT")),
                Integer.parseInt(properties.getProperty("LISTING_REQUESTS_PER_MINUTE_CONSTRAINT")),
                Integer.parseInt(properties.getProperty("LISTING_TIMEOUT_DURATION_IN_SECONDS")), metricRegistry);
    }

    @Provides
    @Singleton
    public ListingApiAccessor provideListingApiAccessor(Client client){
        return new ListingApiAccessor(client, new GsonBuilder(), new StubHubListingDeserializer());
    }

    @Provides
    @Singleton
    public StubHubEventDao provideEventDao(DynamoDB dynamoDB) {
        return new StubHubDynamoDbEventDao(dynamoDB, new AttributeUpdateFactory());
    }

    @Provides
    @Singleton
    public DynamoDB provideDynamoDB(AmazonDynamoDB amazonDynamoDB){
        return new DynamoDB(amazonDynamoDB);
    }

    @Provides
    @Singleton
    public AmazonDynamoDB provideAmazonDynamoDB(AWSCredentials awsCredentials){
        return new AmazonDynamoDBClient(awsCredentials);
    }

    @Provides
    @Singleton
    public AWSCredentials provideAWSCredentials(){
        return new BasicAWSCredentials(properties.getProperty("AWS_ACCESS_KEY"), properties.getProperty("AWS_SECRET_KEY"));
    }

    @Provides
    @Singleton
    public MetricRegistry provideMetricRegistry() {
        return new MetricRegistry();
    }
}
