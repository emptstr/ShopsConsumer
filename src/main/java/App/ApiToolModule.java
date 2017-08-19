package App;

import com.google.inject.AbstractModule;

/**
 * @author Jordan Gaston
 * @version 0.1.17
 */
public class ApiToolModule extends AbstractModule {

    protected void configure() {
    bind(ApiToolRunner.class).to(ApiToolRunner.class);
    }
}
