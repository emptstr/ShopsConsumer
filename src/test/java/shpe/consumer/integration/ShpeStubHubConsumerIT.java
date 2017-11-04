package shpe.consumer.integration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import shpe.consumer.app.ConsumerModule;
import shpe.util.configuration.ConfigurationRepositoryFactory;
import shpe.util.configuration.ConfigurationRepositoryFactory.RuntimeContext;

import java.util.ArrayList;
import java.util.List;

import static shpe.util.configuration.ConfigurationRepositoryFactory.RuntimeContext.*;

public class ShpeStubHubConsumerIT {

    private static final String PORT = System.getProperty("dynamodb.port");
    private static final String TABLE_NAME = System.getProperty("table.name");
    private static final String EVENT_KEY_NAME = System.getProperty("event.key.name");

    private AmazonDynamoDB dynamoDB;

    @Before
    public void setup() {
        Injector injector = Guice.createInjector(new ConsumerModule(ConfigurationRepositoryFactory.getConfigurationRepository(RuntimeContext.TEST)));
        this.dynamoDB = createDynamoDB(injector);
    }

    @Test(timeout = 10000L)
    public void shouldRunApp() throws Exception {
        while(true) {
            DescribeTableResult describeTableResult = dynamoDB.describeTable(TABLE_NAME);
            TableDescription tableDescription = describeTableResult.getTable();
            long itemCount = tableDescription.getItemCount();
            if (itemCount > 0L) {
                return;
            }
                Thread.sleep(1000L);
        }
    }

    @After
    public void tearDown(){
        cleanUpDynamoDB();
    }

    private void cleanUpDynamoDB(){
        try {
            dynamoDB.deleteTable(new DeleteTableRequest()
                    .withTableName(TABLE_NAME));
        }catch(Exception e) {
            throw new RuntimeException("Failed while cleaning up DynamoDB");
        }finally {
            dynamoDB.shutdown();
        }
    }

    private AmazonDynamoDB createDynamoDB(Injector injector) {
        AmazonDynamoDB dynamoDB = new AmazonDynamoDBClient(injector.getInstance(AWSCredentials.class));
        System.out.println("port:" + PORT);
        dynamoDB.setEndpoint(String.format("http://localhost:%s/", PORT));
        createEventTable(dynamoDB, TABLE_NAME, EVENT_KEY_NAME);
        return dynamoDB;
    }

    private void createEventTable(AmazonDynamoDB dynamoDB, String tablename, String keyname) {
        try {
            CreateTableResult createTableResponse = dynamoDB.createTable(new CreateTableRequest()
                    .withTableName(tablename)
                    .withKeySchema(new KeySchemaElement().withKeyType(KeyType.HASH).withAttributeName(keyname))
                    .withAttributeDefinitions(new AttributeDefinition().withAttributeType(ScalarAttributeType.S).withAttributeName(keyname))
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withWriteCapacityUnits(1L)
                            .withReadCapacityUnits(1L))
            );

            System.out.println(String.format("Table Ready: %s", createTableResponse));
        } catch (Exception e) {
            throw new RuntimeException("Integration test failed while creating dynamo table", e);
        }
    }
}
