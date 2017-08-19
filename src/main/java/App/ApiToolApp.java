package App;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ApiToolApp
 * creates and invokes ApiToolRunner application runner
 *
 * @author Jordan Gaston
 * @version 0.1.17
 */
@Singleton
public class ApiToolApp {

    private final static Logger logger = Logger.getLogger(ApiToolApp.class.getName());

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ApiToolModule());
        ApiToolRunner runner = injector.getInstance(ApiToolRunner.class);
        logger.log(Level.INFO, "Starting ApiToolApp");
        runner.run();
        logger.log(Level.INFO, "ApiToolApp Finished");
    }
}