package shpe.consumer.dao;

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;

public class AttributeUpdateFactory {

    public AttributeUpdate getInstance(String attributeName){
        return new AttributeUpdate(attributeName);
    }
}
