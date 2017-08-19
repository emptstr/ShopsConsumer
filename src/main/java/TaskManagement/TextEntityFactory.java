package TaskManagement;

import TaskManagement.EntityFactory;

import javax.ws.rs.client.Entity;

/**
 * Created by jordan on 6/14/17.
 */
public class TextEntityFactory<String> implements EntityFactory<String> {
    public Entity<String> getEntity(String entityString) {
        return Entity.text(entityString);
    }
}
