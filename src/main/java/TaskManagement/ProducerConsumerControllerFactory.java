package TaskManagement;

import java.util.List;

/**
 * Created by jordan on 6/16/17.
 */
public interface ProducerConsumerControllerFactory {

    public ProducerConsumerController getInstance(List<? extends Producer<?>> producers, List<? extends Consumer<?>> consumers);
}
