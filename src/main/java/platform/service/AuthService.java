package platform.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import platform.auth.JwtService;
import platform.auth.Role;
import platform.auth.TokenTypes;
import platform.domain.Tokens;
import platform.domain.User_Auth;
import platform.domain.User_Info;
import platform.dto.authdto.AuthenticationRequestDTO;
import platform.dto.authdto.AuthenticationResponseDTO;
import platform.repository.TokensRepository;
import platform.repository.UserAuthRepository;
import platform.repository.UserInfoRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static platform.constants.DirectoryMapConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserAuthRepository userAuthRepository;
    private final UserInfoRepository userInfoRepository;
    private final TokensRepository tokensRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public boolean isPrincipal(){
        Authentication auth = getAuthentication();
        String principal = auth.getPrincipal().toString();
        if(principal == null || principal.equals("anonymousUser")){
            return false;
        }
        return true;
    }

    public boolean isCurrentUser(String userId){
        String principal = getUserPrincipalOrThrow();
        log.info("AuthService isCurrentUser : principal : {}", principal);
        if(userId.equals(principal)){
            return true;
        }
        return false;
    }

    public String getUserPrincipalOrThrow() throws AccessDeniedException {
        Authentication auth = getAuthentication();
        String principal = auth.getPrincipal().toString();
        if(principal == null || principal.equals("anonymousUser")){
            throw new AccessDeniedException("User login cannot be verified");
        }
        return principal;
    }

    private Authentication getAuthentication(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth;
    }

    public boolean isAdmin(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().map(s -> s.toString())
                .anyMatch(s -> s.equals("ROLE_" + Role.ADMIN_MAIN) || s.equals("ROLE_" + Role.ADMIN_SUB));
        return isAdmin;
    }

    // Edit so that it doesn't return a ResponseDTO!
    public AuthenticationResponseDTO register(@NonNull HttpServletRequest request, @NonNull AuthenticationRequestDTO requestDTO) throws BadRequestException {
        final String clientIp = request.getRemoteAddr();
        User_Auth userAuth = userAuthRepository.findById(requestDTO.getUserId()).orElse(null);
        User_Auth newUser;
        // If not null, it means a user with the requested userId already exists - cannot register new user.
        if(userAuth == null){
            newUser = userAuthRepository.saveAndFlush(User_Auth.builder()
                    .username(requestDTO.getUserId())
                    .password(passwordEncoder.encode(requestDTO.getPassword()))
                    .role(Role.USER_INDIVIDUAL)
                    .build());
        }  else {
            log.info("AuthService register : User already exists");
            throw new BadRequestException("This user ID already exists, please use a different ID");
        }

        HashMap<String, Object> claims = new HashMap<>();
        claims.put(Claims.ISSUER, PLATFORM_SERVER_SOCKET_ADDR);
        claims.put(Claims.AUDIENCE, PLATFORM_SERVER_IP_ADDR);

        // Explicitly add user to "user_info" table as well
        userInfoRepository.saveAndFlush(User_Info.builder()
                .userId(requestDTO.getUserId()).build());
        log.info("AuthService register - save to userInfoRepository");
        String accessToken = jwtService.generateToken(claims, newUser);
        String refreshToken = jwtService.generateRefreshToken(claims, newUser);
        saveUserToken(newUser, refreshToken, clientIp);

        return AuthenticationResponseDTO.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    public AuthenticationResponseDTO authenticate(@NonNull HttpServletRequest request, @NonNull AuthenticationRequestDTO requestDTO){
        log.info("AuthService authenticate : {} | {}", requestDTO.getUserId(), requestDTO.getPassword());
        final String clientIp = request.getRemoteAddr();

//        The "AuthenticationProvider" implementation to be used by the AuthenticationManager is defined in
//        the "SecurityBeansConfig.java" file. Provide the correct auth and the AuthenticationProvider
//        implementation will handle the rest (along with other security classes registered as spring beans).
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestDTO.getUserId(), requestDTO.getPassword()));
        log.info("Authentication Successful");

//        The auth itself is complete already. However, the User_Auth entity is required
//        to generate the JWT tokens
        User_Auth userAuth = userAuthRepository.findById(requestDTO.getUserId()).orElseThrow(
                () -> new UsernameNotFoundException("User not found for username : " + requestDTO.getUserId()));
        log.info("User Auth information found from DB");

        // If the account is banned, revoke login attempt
        // 계정이 밴 당한 상태면, 로그인을 거절하고, 거절당했다는 메시지 송출
        if(!userAuth.isEnabled()){
            throw new AccessDeniedException("This account has been banned. Please contact support for more details.");
        }
        log.info("AuthService authenticate : UserAuth = {}", userAuth.getUsername());

        HashMap<String, Object> claims = new HashMap<>();
        claims.put(Claims.ISSUER, PLATFORM_SERVER_SOCKET_ADDR);
        claims.put(Claims.AUDIENCE, PLATFORM_SERVER_IP_ADDR);

        String accessToken = jwtService.generateToken(claims, userAuth);
        String refreshToken = jwtService.generateRefreshToken(claims, userAuth);

        // Take a moment to think about this. This doesn't allow logging in from multiple devices!
        // Find a method to distinguish tokens that are logged in from different devices
        revokeAllUserTokens(userAuth, clientIp);
        saveUserToken(userAuth, refreshToken, clientIp);
        return AuthenticationResponseDTO.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    public AuthenticationResponseDTO refreshTokenFromCookie(@NonNull HttpServletRequest request,
                                                            @NonNull HttpServletResponse response) throws IllegalStateException, NullPointerException, UsernameNotFoundException{
        log.info("AuthService refreshTokenFromCookie");
        final String clientIp = request.getRemoteAddr();

        HashMap<String, Object> claims = new HashMap<>();
        claims.put(Claims.ISSUER, PLATFORM_SERVER_SOCKET_ADDR);
        claims.put(Claims.AUDIENCE, PLATFORM_SERVER_IP_ADDR);

        final String newAccessToken;
        final String newRefreshToken;
        final String userId;
        String oldRefreshToken = null;
        try {
            for(Cookie c : request.getCookies()){
                if(c.getName().equals("refreshToken")){
                    log.info("Cookie : " + c.getName() + " = " + c.getValue());
                    oldRefreshToken = c.getValue();
                }
            }
            userId = jwtService.extractUsername(oldRefreshToken);
        } catch (IllegalArgumentException e) {
            log.info("Refresh token cookie is null");
            throw new IllegalArgumentException("Refresh token cookie is null");
        } catch (IllegalStateException e) {
            log.info("Refresh token is not valid");
            throw new IllegalStateException("Refresh token is not valid");
        }

        Tokens repoToken = tokensRepository.findById(oldRefreshToken).orElse(null);
        if(!userId.equals(repoToken.getUserAuth().getUsername())){
            throw new IllegalStateException("No matches found for the provided refresh token");
        }

        User_Auth userAuth = userAuthRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User not found for username : " + userId));

        if(jwtService.isTokenValid(oldRefreshToken, clientIp, userAuth)) {
            newAccessToken = jwtService.generateToken(claims, userAuth);
            newRefreshToken = jwtService.generateRefreshToken(claims, userAuth);
            revokeAllUserTokens(userAuth, clientIp);
            saveUserToken(userAuth, newRefreshToken, clientIp);
            AuthenticationResponseDTO authResponse = AuthenticationResponseDTO.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build();
            return authResponse;
        }
        else{
            log.info("Invalid refresh token, please login again");
            throw new IllegalStateException("Invalid refresh token, please login again");
        }
    }

    public ResponseCookie setResponseCookie(String name, String value, String domain){
        return ResponseCookie.from(name, value)
                .maxAge(Duration.ofMinutes(5))
                .path("/") // Allow cookies to all APIs of this server
                .domain(domain)
                .sameSite("Strict") // If sameSite = None, secure=true // Else, secure = false
                .secure(false)
                .httpOnly(false)
                .build();
    }

    public void setCookies(HttpHeaders responseHeader, String cookieName, String cookieValue){
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setHttpOnly(true);

        responseHeader.add(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void saveUserToken(User_Auth userAuth, String jwtToken, String clientIp){
        log.info("AuthService saveUserToken");
        Tokens token = Tokens.builder()
                .token(jwtToken)
                .tokenType(TokenTypes.BEARER)
                .timestamp(LocalDateTime.now())
                .userAuth(userAuth)
                .clientIp(clientIp)
                .build();
        tokensRepository.save(token);
    }

    // Potentially change this method to "revoke" instead of delete
    private void revokeAllUserTokens(User_Auth userAuth, String clientIp){
        log.info("AuthService revokeAllUserTokens");
        List<Tokens> validUserTokens = tokensRepository.findAllByUserAuth_UsernameAndClientIp(userAuth.getUsername(), clientIp);
        if (validUserTokens.isEmpty()){return;}
        tokensRepository.deleteAll(validUserTokens);
    }

    private void deleteAllUserTokens(User_Auth userAuth, String clientIp){
        log.info("AuthService deleteAllUserTokens");
        List<Tokens> validUserTokens = tokensRepository.findAllByUserAuth_UsernameAndClientIp(userAuth.getUsername(), clientIp);
        if (validUserTokens.isEmpty()){return;}
        tokensRepository.deleteAll(validUserTokens);
    }

    public void deleteUser(String userId) throws BadRequestException {
        User_Auth userAuth = userAuthRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found - Invalid IDs"));

        userAuthRepository.delete(userAuth);
        userInfoRepository.deleteById(userId);
    }
}
