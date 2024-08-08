package platform.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import platform.domain.Tokens;
import platform.repository.TokensRepository;

import static platform.auth.AuthHeaderConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final JwtService jwtService;
    private final TokensRepository tokensRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IllegalArgumentException, IllegalStateException{
        // The code will receive null auth from the constructor anyways -> explicitly set to null to prevent security
        // SecurityContext holds the auth info, and this is what the LogoutService will be using
        // instead of the Authentication entity in the constructor parameter
        authentication = null;

        log.info("LogoutService Initiated");
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String accessToken;
        final String userId;
        String refreshToken = null;

        if(request.getCookies()!=null){
            try {
                for(Cookie c : request.getCookies()){
                    if(c.getName().equals("refreshToken")){
                        log.info("LogoutService CCookie : {} = {}", c.getName(), c.getValue());
                        refreshToken = c.getValue();
                    }
                }
            } catch (IllegalStateException e) {
                throw new IllegalStateException("LogoutService : Refresh Token is not valid");
            }
        }

        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            return;
        }

        accessToken = authHeader.split(" ")[1].trim();
        userId = jwtService.extractUsername(accessToken);

        log.info("LogoutService : userId : {}", userId);
        log.info("LogoutService : accessToken : {}", accessToken);
        log.info("LogoutService : refreshToken : {}", refreshToken);
        if(refreshToken!=null){
            Tokens storedToken = tokensRepository.findById(refreshToken).orElse(null);
            if (storedToken != null) {
                log.info("LogoutService : SecurityContext : " + SecurityContextHolder.getContext().toString());
                tokensRepository.delete(storedToken);
            }
        }
        SecurityContextHolder.clearContext();
    }
}
