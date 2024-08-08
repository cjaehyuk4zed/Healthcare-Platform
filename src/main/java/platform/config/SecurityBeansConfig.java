package platform.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

import platform.repository.UserAuthRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityBeansConfig {

    private final UserAuthRepository userAuthRepository;


    @Bean
    public DelegatingSecurityContextRepository delegatingSecurityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository()
        );
    }

    /**  UserDetailsManager is not necessary as CRUD will be done by JPA and DB transaction
     *  UserDetailsService will now return an instance of User_Auth (DB table entity which implements the UserDetails interface)
     *
     *  Also note that the lambda expression using `username -> userAuthRepository.findById(username)`
     *  is a shorthand way to define the `loadUserByUsername(String username)` method from the UserDetailsService interface
     *  Without this lambda, a separate implementation class would be needed to implement the `loadByUsername(String username)` method (using @Override)
     */
    @Bean
    public UserDetailsService userDetailsService(){
        UserDetailsService userDetailsService = username -> userAuthRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for username : " + username));
        return userDetailsService;
    }

    /**
     *  Use BCyrptPasswordEncoder. The password is saved to the DB User_Auth table in its encrypted state
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

/**     "ProviderManager" class, which implements the "AuthenticationManager" interface, iterates through
 *    a list of AuthenticationProvider implementations. This bean indicates that we will be using
 *    DaoAuthenticationProvider as the implementation to be delivered to the AuthenticationManager.
 *
 *    DaoAuthentication = Auth which uses a Data Access Object (DAO) to retrieve user info from a Relational Database
 *    It leverages UserDetailsService (as a DAO) in order to search the Username, Password and GrantedAuthority(s).
 */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // AuthenticationManager is not publicly accessible by default. Register as @Bean to explicitly expose it
    @Bean
    public AuthenticationManager authenticationManager(@NonNull AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
