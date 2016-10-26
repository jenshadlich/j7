package de.jeha.j7;

import de.jeha.j7.config.J7Configuration;
import de.jeha.j7.resources.ProxyResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ServerProperties;

import java.util.Locale;
import java.util.logging.Logger;

/**
 * @author jenshadlich@googlemail.com
 */

public class J7Application extends Application<J7Configuration> {

    private static final String APPLICATION_NAME = "j7";

    public static void main(String... args) throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        new J7Application().run(args);
    }

    @Override
    public String getName() {
        return APPLICATION_NAME;
    }

    @Override
    public void initialize(Bootstrap<J7Configuration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(J7Configuration configuration, Environment environment) {

        environment.jersey().register(new ProxyResource());

        // print request / response headers
        // printEntity=false because printing consumes the input stream and breaks object creation (PUT Bucket)
        environment.jersey().register(new LoggingFilter(Logger.getLogger("InboundRequestResponse"), false));

        environment.jersey().disable(ServerProperties.WADL_FEATURE_DISABLE);
        environment.jersey().disable(ServerProperties.LOCATION_HEADER_RELATIVE_URI_RESOLUTION_DISABLED);
        environment.jersey().disable(ServerProperties.JSON_PROCESSING_FEATURE_DISABLE);
    }

}
