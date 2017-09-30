package shpe.consumer.integration;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;
import shpe.consumer.app.ConsumerModule;

public class ShpeStubHubConsumerIT {
    @Test
    public void shouldRunApp(){
        Injector injector = Guice.createInjector(new ConsumerModule());
        AmazonDynamoDB dynamoDB = injector.getInstance(AmazonDynamoDB.class);
        dynamoDB.setEndpoint("http://localhost:3000");
    }
}
