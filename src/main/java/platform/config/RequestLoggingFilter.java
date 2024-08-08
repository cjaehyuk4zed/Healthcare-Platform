package platform.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import platform.domain.Request_Log;
import platform.repository.RequestLogRepository;

import java.io.IOException;

/**
 * Filter to log all incoming HTTP requests for easier debugging
 * Logs the following : [IP, User, RequestURL]
 * Saves logs in the "request_log" table in MySQL
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RequestLoggingFilter extends OncePerRequestFilter {

    private final RequestLogRepository requestLogRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String principal;

        if(SecurityContextHolder.getContext().getAuthentication() == null){
            principal = "Non Logged-in User";
        } else{
            principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        }

        final String remoteAddr = request.getRemoteAddr();
        final String userId = principal;
        final String requestURL = String.valueOf(request.getRequestURL());


        try {
            filterChain.doFilter(request, response);
        } finally {
            log.info("Logging current request from : \n\t - IP : {} \n\t - User : {} \n\t - Request URL : {}", remoteAddr, userId, requestURL);
            requestLogRepository.save(new Request_Log(userId, requestURL));
        }
    }

}
