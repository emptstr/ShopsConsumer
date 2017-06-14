package App;

import com.google.inject.AbstractModule;

/**
 * Created by jordan on 5/29/17.
 */
public class ApiToolModule extends AbstractModule {

    protected void configure() {
    bind(ApiToolRunner.class).to(ApiToolRunner.class);
    }
}
