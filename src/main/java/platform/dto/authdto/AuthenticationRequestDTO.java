package platform.dto.authdto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthenticationRequestDTO {
    @Schema(example = "admin")
    private String userId;
    @Schema(example = "admin")
    private String password;
}
