package platform.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import platform.auth.JwtAuthenticationFilter;

import static platform.auth.Role.*;

@Configuration
@EnableWebSecurity
// Default settings for @EnableMethodSecurity, possibly change to true for more security once implemented
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = false, jsr250Enabled = false, proxyTargetClass = false)
@RequiredArgsConstructor
public class SecurityConfig{
    // TOKEN 검증 코드를 필터에 추가해야 된다
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final DelegatingSecurityContextRepository delegatingSecurityContextRepository;
    private final RequestRateLimitFilter requestRateLimitFilter;
    private final RequestLoggingFilter requestLoggingFilter;
    private final LogoutHandler logoutHandler;

    private static final String[] SWAGGER_WHITELIST = {
        // Swagger UI v3
        "/v3/api-docs/**",
        "v3/api-docs/**",
        "/swagger-ui/**",
        "swagger-ui/**"
    };

    // DELETE THIS ONE LATER ONCE MIGRATION TO API V2 IS COMPLETE
    public static final String[] AUTH_WHITELIST_V1 = {
            // auth
            "/api/auth/**",
            // get REMOVE THESE LATER
            "/api/file/get/**",
            "/api/post/get/**",
            "/api/user/get/**"
    };

    // Allow ALL HTTP requests from non-logged-in users
    public static final String[] AUTH_WHITELIST_V2_HTTP_ALL = {
        // test
        "/api/v2/test/**",
        // auth
        "/api/auth/**",
        "/api/v2/auth/**"
    };


    // Allow HTTP GET requests from non-logged-in users
    public static final String[] AUTH_WHITELIST_V2_HTTP_GET = {
        "/api/v2/postings/**",
        "/api/v2/users/**",
        "/api/v2/header/**",
        "/api/v2/personals/**",
        "/api/v2/images/**"
    };

    private static final IpAddressMatcher jenkinsIpAddress = new IpAddressMatcher("192.168.0.80"); // 건혁 IP
    private static final IpAddressMatcher serverIpAddress = new IpAddressMatcher("192.168.0.79"); // 서버 IP
    private static final IpAddressMatcher profIpAddress = new IpAddressMatcher("192.168.0.56"); // 교수님 IP


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf((csrf) -> csrf.disable())
                .headers((header)->header.frameOptions((fo)->fo.sameOrigin()))
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .requestMatchers(AUTH_WHITELIST_V1).permitAll()
                        .requestMatchers(AUTH_WHITELIST_V2_HTTP_ALL).permitAll()
                        .requestMatchers(HttpMethod.GET, AUTH_WHITELIST_V2_HTTP_GET).permitAll()
                        .requestMatchers("/api/v2/admin-controller/**").hasAnyRole(ADMIN_MAIN.name(), ADMIN_SUB.name())
                        .requestMatchers("/api/**").authenticated()
//                        .requestMatchers("/api/file").hasAnyRole(ADMIN_MAIN.name(), ADMIN_SUB.name(), USER_ENTERPRISE.name(), USER_INDIVIDUAL.name())
//                        .requestMatchers("/**").hasAnyRole(ADMIN_MAIN.name(), ADMIN_SUB.name(), USER_ENTERPRISE.name(), USER_INDIVIDUAL.name(), VISITOR.name(), GUEST.name())
                        .anyRequest().denyAll());
        // .hasAnyRole()를 쓰면 Spring Security의 default prefix로 ROLE_ prefix가 붙게 된다. 따라서 hasAnyAuthority() 메서드를 사용한다.

        /**
         * Set `RequestAttributeSecurityContextRepository` as the DelegatingSecurityContextRepository
         * The SecurityContext now requires explicit saving of the SecurityContext per request.
         * This is suitable for using JWT Tokens with HTTP stateless management (sessionId=null)
         * The explicit saving is done in the JWTAuthenticationFilter
         */
        http.securityContext((securityContext) -> securityContext.requireExplicitSave(true).securityContextRepository(delegatingSecurityContextRepository));

        http.sessionManagement((sessionManagement) -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(requestRateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(requestLoggingFilter, RequestRateLimitFilter.class);

        http.logout((logout) -> logout.logoutUrl("/api/v2/auth/logout").addLogoutHandler(logoutHandler)
                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()));
        return http.build();
    }
}

