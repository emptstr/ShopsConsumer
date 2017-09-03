package shpe.util;

/**
 * Created by jordan on 6/20/17.
 */
public interface Timer {

    public void start();

    public long getElapsedSecs();

    public long getElapsedMillis();

    public void reset();

}

