package shpe.consumer.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ConsumerApp
 * creates and invokes ConsumerRunnable application runner
 *
 * @author Jordan Gaston
 * @version 0.1.17
 */
@Singleton
public class ConsumerApp {
    private final static Logger logger = Logger.getLogger(ConsumerApp.class.getName());
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ConsumerModule());
        ConsumerRunnable runner = injector.getInstance(ConsumerRunnable.class);
        logger.log(Level.INFO, "Starting ConsumerApp");
        runner.run();
        logger.log(Level.INFO, "ConsumerApp Finished");
    }
}