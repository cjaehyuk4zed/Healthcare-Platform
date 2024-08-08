package platform.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

import static platform.auth.AuthHeaderConstants.BEARER;
import static platform.config.SecurityConfig.AUTH_WHITELIST_V1;

/**
 * JwtAuthenticationFilter
 * Http Request 를 가로채어, UsernamePasswordAuthenticationToken 을 생성하여 다음 Filter에 전달합니다.
 *
 */

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // TOKEN 검증 코드를 필터에 추가해야 된다
    // TOKEN 검증 코드를 필터에 추가해야 된다
    // TOKEN 검증 코드를 필터에 추가해야 된다
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        for(String whitelisted_api : AUTH_WHITELIST_V1){
            if(request.getServletPath().contains(whitelisted_api)){
                filterChain.doFilter(request, response);
                return;
            }
        }

        /**
         *  Code for logging which cookies were received
         *  Delete when the code is stable and logging is no longer necessary
         */
        log.info("JwtFilter : JWT Filter start");
        if(request.getCookies()!= null){
            for(Cookie c : request.getCookies()){
                if(c.getName() != null && c.getValue() != null){
                    log.info("JwtFilter : Cookies : " + c.getName() + "=" + c.getValue());
                } else {
                    log.info("JwtFilter : Cookies : null cookie");
                }
            }
        }


        /**
         * Remove all logs when they are no longer needed, as the logs below may contain sensitive information
         */
        log.info("JwtFilter : Starting SecurityContext is : " + SecurityContextHolder.getContext());
        HashMap<String, String> cookies = new HashMap<>();
        if(request.getCookies()!= null){
            for(Cookie c : request.getCookies()){
                if(c.getName() != null && c.getValue() != null){
                    cookies.put(c.getName(), c.getValue());
                }
            }
        }

        String refreshToken = cookies.get("refreshToken");
        final String accessToken;
        final String userId;
        final String clientIp = request.getRemoteAddr();
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("JwtFilter : AuthHeader = " + authHeader);

//         Check if JWT token is in "Bearer Authentication" format
        if(authHeader == null || !authHeader.startsWith(BEARER)){
            log.info("JwtFilter : Authorization header is either null or doesn't contain HTTP Bearer auth");
            filterChain.doFilter(request, response);
            return;
        }

        /**
         * Fetch JWT token from HTTP Bearer Authorization
         * Validate JWT Token using UserDetails from User_Auth (DB table entity which implements the UserDetails interface)
         * Explicitly save SecurityContext, and set auth details in the SecurityContext
         * (Spring automatically creates an empty SecurityContext if none exists when running the `SecurityContextHolder.getContext()` method)
         */
        // Remove "Bearer " header and get the JWT token
        accessToken = authHeader.split(" ")[1].trim();
        userId = jwtService.extractUsername(accessToken);
        log.info("JwtFilter : JWT token received, userId = " + userId);

        // Validate the JWT token
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null){
            // userDetails will hold an instance of User_Auth (DB table entity which implements the UserDetails interface)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userId);

            // If JWT token is valid, configure Spring Security to set auth
            if(jwtService.isTokenValid(accessToken, clientIp, userDetails)){
//                log.info("JwtFilter : Authorities are " + userDetails.getAuthorities());
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken( userId, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Creates an empty SecurityContext if none exists and set the auth
                SecurityContextHolder.getContext().setAuthentication(authToken);
//                log.info("JwtFilter : SecurityContext is : " + SecurityContextHolder.getContext());
            }

        }
        filterChain.doFilter(request, response);
    }
}
