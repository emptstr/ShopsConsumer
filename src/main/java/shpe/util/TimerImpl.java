package shpe.util;

/**
 * Created by jordan on 9/4/17.
 */
public class TimerImpl implements Timer {

    private long startTime = 0;

    @Override
    public void start() {
        if(startTime ==0) {
            this.startTime = System.currentTimeMillis();
        }
    }

    @Override
    public long getElapsedSecs() {
       return  getElapsedMillis()/1000;
    }

    @Override
    public long getElapsedMillis() {
       return  (System.currentTimeMillis() - startTime);
    }

    @Override
    public void reset() {
        this.startTime = System.currentTimeMillis();
    }
}
