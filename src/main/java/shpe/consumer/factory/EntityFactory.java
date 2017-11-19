package shpe.consumer.factory;

import javax.ws.rs.client.Entity;

/**
 * Created by jordan on 9/3/17.
 */
public interface EntityFactory<T> {
    public Entity<T> getEntity(T bodyString, String mediaType);
}
