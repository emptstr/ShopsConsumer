package TaskManagement;

import javax.ws.rs.client.Entity;

/**
 * Created by jordan on 6/14/17.
 */
public interface EntityFactory<T> {

    public Entity<T> getEntity(T entity);


}
