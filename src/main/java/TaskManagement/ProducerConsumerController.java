package TaskManagement;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by jordan on 6/16/17.
 */
public abstract class ProducerConsumerController {

    private final List<Producer<?>> producers;
    private final List<Consumer<?>> consumers;
    private final ExecutorService threadPool;

    protected ProducerConsumerController(List<Producer<?>> producers, List<Consumer<?>> consumers, ExecutorService threadPool) {
        this.producers = producers;
        this.consumers = consumers;
        this.threadPool = threadPool;
    }

    public abstract void execute();
}
