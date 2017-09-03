package shpe.consumer.factory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

/**
 * Created by jordan on 9/3/17.
 */
public class StringEntityFactory implements EntityFactory<String> {
    @Override
    public Entity<String> getEntity(String bodyString, String mediaType) {
        return Entity.entity(bodyString, mediaType);
    }
}
