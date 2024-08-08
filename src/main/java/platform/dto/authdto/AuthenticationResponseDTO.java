package platform.dto.authdto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthenticationResponseDTO {
    private String accessToken;
    private String refreshToken;
}
