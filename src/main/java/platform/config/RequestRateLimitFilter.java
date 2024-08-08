package platform.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static platform.auth.AuthHeaderConstants.BEARER;

/**
 * Filter to limit the number of API requests that a single IP can make
 * Limit number is defined in 'application.properties'
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RequestRateLimitFilter extends OncePerRequestFilter {

    private final ConcurrentHashMap<String, AtomicInteger> sessionCounters = new ConcurrentHashMap<>();

    // Edit values in application.properties
    @Value("${server.http.request-rate-limit}")
    private Integer limit; // Maximum allowed requests for each token per time window

    @Value("${server.http.request-rate-duration}")
    private Long duration; // Time window (in seconds) for the rate limit

    String atomicKey = null;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("RequestRateLimitFilter : AuthHeader = " + authHeader);

        if(authHeader == null || !authHeader.startsWith(BEARER)){
            // Set request limit by IP address
            atomicKey = request.getRemoteAddr();
        } else{
            // Remove "Bearer " header and get the JWT token
            atomicKey = authHeader.split(" ")[1].trim();
        }

        log.info("RequestRateLimitFilter : atomicKey (HttpRequestAddr) = " + atomicKey);

        AtomicInteger counter = sessionCounters.computeIfAbsent(atomicKey, k -> new AtomicInteger());

        if (counter.incrementAndGet() > limit) {
            log.info("RequestRateLimitFilter : HTTP Request Rate limit exceeded for this session.");
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("HTTP Request Rate limit exceeded for this session.");
            return;
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Reset the counter after the duration
            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    sessionCounters.remove(atomicKey);
                }
            }, duration * 1000);
        }
    }
}
