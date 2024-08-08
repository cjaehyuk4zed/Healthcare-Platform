package platform.controller;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import platform.domain.User_Ip;
import platform.dto.authdto.AuthenticationRequestDTO;
import platform.dto.authdto.AuthenticationResponseDTO;
import platform.repository.UserIpRepository;
import platform.service.AuthService;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "로그인 및 인증 관련 APIs")
@Hidden
public class AuthController {

    private final AuthService authService;
    private final UserIpRepository userIpRepository;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully registered as new user", content = {
                    @Content(mediaType = "application/text", schema = @Schema(example = "JWT accessToken String"))
            }),
            @ApiResponse(responseCode = "409", description = "HTTP Conflict Error : User already exists with this username")
    })
    @Operation(summary = "회원가입", description = "<p> userId와 password 로 회원가입. User_Auth 객체를 생성하며, 기본 권한을 부여한다.</p>" +
            "<p>Users API 에서 상세 회원정보를 기입하면 User_Info 객체를 생성하며, 추가 권한이 부여될 수도 있다 (e.g. 공급자 등록시 추가 권한).</p>")
    @PostMapping("/register")
    public ResponseEntity<String> register(HttpServletRequest request, HttpServletResponse response, @RequestBody AuthenticationRequestDTO requestDTO) throws BadRequestException {
        AuthenticationResponseDTO responseDTO = authService.register(request, requestDTO);
        if(responseDTO != null){
            String accessTokenString = responseDTO.getAccessToken();
            ResponseCookie refreshTokenCookie = authService.setResponseCookie("refreshToken", responseDTO.getRefreshToken(), request.getServerName());

            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

            return ResponseEntity.ok().body(accessTokenString);
        } else{
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = {
                    @Content(mediaType = "application/text", schema = @Schema(example = "JWT accessToken String, refreshToken stored within Cookie"))
            }),
            @ApiResponse(responseCode = "403", description = "Username or Password is incorrect")
    })
    @Operation(summary = "User login - 로그인", description = "<p>성공시 accessToken 과 refreshToken 을 발급. accessToken은 ResponseBody에, refreshToken은 HTTP Cookie에 발급한다.</p>" +
            "<p>동시에 refreshToken은 DB에 저장해놓는다. 로그인 실패시 HTTP 403 에러를 반환한다.</p>")
    @PostMapping("/login")
    public ResponseEntity<String> authenticate(HttpServletRequest request, HttpServletResponse response, @RequestBody AuthenticationRequestDTO requestDTO){
        log.info("AuthController /login : " + requestDTO);

        AuthenticationResponseDTO responseDTO = authService.authenticate(request, requestDTO);

//        User_Ip userIp = userIpRepository.findByUserIpCompositeKey_UserIdAndUserIpCompositeKey_IpAddr(requestDTO.getUserId(), request.getRemoteAddr())
//                .orElse(null);
//        if(userIp==null){
//            log.info("New Ip address login for user : " + requestDTO.getUserId());
////            userIpRepository.save(new User_Ip(requestDTO.getUserId(), request.getRemoteAddr()));
//        }



        String accessTokenString = responseDTO.getAccessToken();
        ResponseCookie refreshTokenCookie = authService.setResponseCookie("refreshToken", responseDTO.getRefreshToken(), request.getServerName());

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok().body(accessTokenString);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Issued new accessToken and refreshToken", content = {
                    @Content(mediaType = "application/text", schema = @Schema(example = "JWT accessToken String, refreshToken stored within Cookie"))
            }),
            @ApiResponse(responseCode = "403", description = "The provided refreshToken is invalid or expired. Please login again.")
    })
    @Operation(summary = "accessToken과 refreshToken 재발급 요청", description = "<p>accessToken 만료시 Cookie 에 있는 refreshToken 을 이용해 새로운 accessToken 과 refreshToken 을 발급한다.</p>" +
            "<p>동시에, 이전에 DB에 저장되어있던 refreshToken 은 삭제하고, 새로운 refreshToken 을 DB에 저장한다. refreshToken 만료시 403 에러를 반환한다.</p>")
    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshToken(HttpServletRequest request, HttpServletResponse response) throws StreamWriteException, DatabindException, IOException {
        try {
            log.info("AuthController /refresh-token");

            AuthenticationResponseDTO responseDTO = authService.refreshTokenFromCookie(request, response);

            String accessTokenString = responseDTO.getAccessToken();
            ResponseCookie refreshTokenCookie = authService.setResponseCookie("refreshToken", responseDTO.getRefreshToken(), request.getServerName());

            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

            return ResponseEntity.ok(accessTokenString);
        } catch (Exception e){
            log.info(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // The logout method is defined in "SecurityConfig.java" by a LogoutHandler and LogoutService
    @Operation(summary = "로그아웃", description = "SpringSecurity 구성 사용, Swagger 용도로 제작한 API. LogoutHandler 수정 필요할 수 있음")
    @PostMapping("/logout")
    public void logout(){
        log.info("AuthController /logout");
    }

}
