package de.jeha.j7.request.tracing;

import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author jenshadlich@googlemail.com
 */
public class RequestTracingServletFilter implements Filter {

    private final static String X_TRACE_ID = "X-Trace-Id";
    private final static Supplier<String> TRACE_ID_SUPPLIER = () -> UUID.randomUUID().toString();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        final Optional<String> traceIdFromRequest =
                Optional.ofNullable(HttpServletRequest.class.cast(request).getHeader(X_TRACE_ID));
        final String traceId = traceIdFromRequest.orElse(TRACE_ID_SUPPLIER.get());

        MDC.put(X_TRACE_ID, traceId);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}