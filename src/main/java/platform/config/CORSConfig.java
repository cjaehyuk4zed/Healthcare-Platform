package platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Override the default CORS Mapping configuration
@Configuration
@EnableWebSecurity
public class CORSConfig implements WebMvcConfigurer{

    //CorsRegistry object is provided as a parameter to the addCorsMappings method. It is used to configure CORS settings.
    @Override
    public void addCorsMappings(CorsRegistry registry){
        // addMapping configures the URL patterns to which the CORS configuration applies. In this case, it is set to apply to all paths ("/**").
        // allowedOrigins method sets the list of origins (domains) allowed to access resources on the server. In this case, the server accepts requests from any domain ("*").
        // allowedMethods method sets the HTTP methods that are allowed for cross-origin requests.
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH");
    }
}
