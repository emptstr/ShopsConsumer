package EventManagement;

/**
 * Created by jordan on 6/22/17.
 */
public interface IndexListFactory {
    public IndexList getInstance(int numEvents, int maxEventsPerRequest, boolean b);
}

