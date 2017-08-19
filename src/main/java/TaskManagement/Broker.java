package TaskManagement;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by jordan on 6/16/17.
 */
public abstract class Broker<T> {

    private final ArrayBlockingQueue<T> taskQueue;
    private final boolean continueProducing =  true;

    public Broker(ArrayBlockingQueue<T> taskQueue) {
        this.taskQueue = taskQueue;
    }

    public T get() throws InterruptedException {
        return this.taskQueue.poll(1, TimeUnit.SECONDS);
    }

    public void put(T addend) throws InterruptedException {
        this.taskQueue.put(addend);
    }
}
