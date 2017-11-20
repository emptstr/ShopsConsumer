package shpe.consumer.app;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.codahale.metrics.MetricRegistry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.apache.commons.codec.binary.Base64;
import shpe.consumer.accessor.*;
import shpe.consumer.controller.*;
import shpe.consumer.dao.AttributeUpdateFactory;
import shpe.consumer.dao.StubHubDynamoDbEventDao;
import shpe.consumer.dao.StubHubEventDao;
import shpe.consumer.dao.TokenSetDaoImpl;
import shpe.consumer.deserializer.LoginApiResultDeserializer;
import shpe.consumer.deserializer.StubHubEventDeserializer;
import shpe.consumer.deserializer.StubHubListingDeserializer;
import shpe.consumer.factory.BasicAuthTokenFactoryImpl;
import shpe.consumer.factory.StringEntityFactory;
import shpe.consumer.generator.TokenSetGenerator;
import shpe.consumer.generator.TokenSetGeneratorImpl;
import shpe.consumer.model.StubHubApiCredentials;
import shpe.consumer.model.StubHubApiLoginResult;
import shpe.consumer.predicate.IsTokenSetValidPredicate;
import shpe.util.configuration.ConfigurationRepositoryFactory;
import shpe.util.configuration.ConfigurationRepositoryFactory.RuntimeContext;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import static shpe.util.configuration.ConfigurationRepositoryFactory.ConfigurationRepository;

/**
 * @author Jordan Gaston
 * @version 0.1.17
 */
public class ConsumerModule extends AbstractModule {


    private final ConfigurationRepository configurationRepository;

    public ConsumerModule(ConfigurationRepository configurationRepository){
        this.configurationRepository = configurationRepository;
    }

    public ConsumerModule(){
        this.configurationRepository = ConfigurationRepositoryFactory.getConfigurationRepository(RuntimeContext.PROD);
    }

    protected void configure() {
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
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.registerTypeAdapter(StubHubApiLoginResult.class, new LoginApiResultDeserializer()).create();
        return new LoginApiAccessorImpl(credentials, client, new BasicAuthTokenFactoryImpl(new Base64()), new StringEntityFactory(), gson);
    }

    @Provides
    @Singleton
    public StubHubApiCredentials provideCredentials(String consumerSecret, String username,
                                                    String password) {
        return new StubHubApiCredentials(configurationRepository.get("CONSUMER_KEY"), configurationRepository.get("CONSUMER_SECRET"),
                configurationRepository.get("USERNAME"), configurationRepository.get("PASSWORD"));
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
        return new EventUpdateControllerImpl(eventRetriever, Integer.parseInt(configurationRepository.get("EVENTS_PER_REQUEST_CONSTRAINT"))
                , Integer.parseInt(configurationRepository.get("REQUESTS_PER_MINUTE_CONSTRAINT")),
                Integer.parseInt(configurationRepository.get("TIMEOUT_DURATION_IN_SECONDS")), metricRegistry);
    }

    @Provides
    @Singleton
    public EventApiAccessor provideEventApiAccessor(Client client){
        return new EventApiAccessorImpl(client, new GsonBuilder(), new StubHubEventDeserializer());
    }

    @Provides
    @Singleton
    public EventListingUpdateController provideEventListingUpdateController(ListingApiAccessor activeListingRetriever, MetricRegistry metricRegistry) {
        return new EventListingUpdateControllerImpl(activeListingRetriever, Integer.parseInt(configurationRepository.get("LISTINGS_PER_REQUEST_CONSTRAINT")),
                Integer.parseInt(configurationRepository.get("LISTING_REQUESTS_PER_MINUTE_CONSTRAINT")),
                Integer.parseInt(configurationRepository.get("LISTING_TIMEOUT_DURATION_IN_SECONDS")), metricRegistry);
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
        return new BasicAWSCredentials(configurationRepository.get("AWS_ACCESS_KEY"), configurationRepository.get("AWS_SECRET_KEY"));
    }

    @Provides
    @Singleton
    public MetricRegistry provideMetricRegistry() {
        return new MetricRegistry();
    }
}