package shpe.util;

/**
 * Created by jordan on 9/4/17.
 */
public class SleeperImpl implements Sleeper {
    @Override
    public void sleep(long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while sleeping", e);
        }
    }
}
