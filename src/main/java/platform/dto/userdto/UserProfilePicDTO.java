package platform.dto.userdto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static platform.constants.DirectoryMapConstants.PLATFORM_SERVER_SOCKET_ADDR;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfilePicDTO {
    @Schema(example = PLATFORM_SERVER_SOCKET_ADDR + "/api/user/profile-pic/admin")
    private String userProfilePicSrc;

    @Schema(example = "admin")
    private String userId;
}
