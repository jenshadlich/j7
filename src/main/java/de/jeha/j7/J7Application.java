package de.jeha.j7;

import de.jeha.j7.config.J7Configuration;
import de.jeha.j7.core.Backend;
import de.jeha.j7.core.Server;
import de.jeha.j7.core.StatusCheckTask;
import de.jeha.j7.health.J7HealthCheck;
import de.jeha.j7.health.ServerHealthCheck;
import de.jeha.j7.request.tracing.RequestTracingServletFilter;
import de.jeha.j7.resources.ProxyResource;
import de.jeha.j7.servlets.VersionServlet;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.http.impl.client.CloseableHttpClient;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author jenshadlich@googlemail.com
 */
public class J7Application extends Application<J7Configuration> {

    private static final Logger LOG = LoggerFactory.getLogger(J7Application.class);

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

        final Backend backend = configuration.getBackend();

        environment.servlets()
                .addFilter("request-tracing-servlet-filter", new RequestTracingServletFilter())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "*");

        environment.jersey().register(new ProxyResource(configuration.getServerSignature(), backend));

        environment.jersey().register(new LoggingFilter(java.util.logging.Logger.getLogger("InboundRequestResponse"), false));

        environment.healthChecks().register(APPLICATION_NAME, new J7HealthCheck(backend));

        environment.jersey().disable(ServerProperties.WADL_FEATURE_DISABLE);
        environment.jersey().disable(ServerProperties.LOCATION_HEADER_RELATIVE_URI_RESOLUTION_DISABLED);
        environment.jersey().disable(ServerProperties.JSON_PROCESSING_FEATURE_DISABLE);

        environment.admin().addServlet("version", new VersionServlet()).addMapping("/version");

        setupStatusChecks(configuration, environment);
    }

    private void setupStatusChecks(J7Configuration configuration, Environment environment) {
        LOG.info("setup status checks ...");

        final Backend backend = configuration.getBackend();
        final CloseableHttpClient httpClient = configuration.buildStatusCheckHttpClient();

        for (Server server : backend.getServers()) {
            final String statusCheckIdentifier =
                    String.format("statusCheck [backend=%s, instance=%s]", backend.getName(), server.getInstance());

            environment
                    .lifecycle()
                    .scheduledExecutorService(statusCheckIdentifier)
                    .build()
                    .scheduleWithFixedDelay(new StatusCheckTask(httpClient, server), 5, 5, TimeUnit.SECONDS);

            environment.healthChecks().register(statusCheckIdentifier, new ServerHealthCheck(server));
        }
    }

}
