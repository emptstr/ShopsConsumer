package TaskManagement;

import java.util.concurrent.BlockingQueue;

/**
 * Created by jordan on 6/16/17.
 */
public abstract class Consumer<T> implements Runnable {

    private final Broker<T> taskQueue;

    public Consumer(Broker<T> taskQueue) {
        this.taskQueue = taskQueue;
    }

    public void run(){
        throw new UnsupportedOperationException("The run method was not overidden");
    }
}
