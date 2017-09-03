package shpe.consumer.app;

import com.google.inject.AbstractModule;

/**
 * @author Jordan Gaston
 * @version 0.1.17
 */
public class ConsumerModule extends AbstractModule {

    protected void configure() {
    bind(ConsumerRunnable.class).to(ConsumerRunnable.class);
    }
}
