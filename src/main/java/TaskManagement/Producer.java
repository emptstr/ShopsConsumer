package TaskManagement;

import java.util.concurrent.BlockingQueue;

/**
 * Created by jordan on 6/15/17.
 */
public abstract class Producer<T> implements Runnable {

    private final Broker<T> broker;

    public Producer(Broker<T> broker){
        this.broker = broker;
    }

    public void run() {
        throw new UnsupportedOperationException("The run method has not been overridden");
    }
}
