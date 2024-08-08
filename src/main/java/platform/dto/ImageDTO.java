package platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static platform.constants.DirectoryMapConstants.IMAGE_CONTROLLER;
import static platform.constants.DirectoryMapConstants.PLATFORM_SERVER_SOCKET_ADDR;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {

    // ImageName = imageId + File Extension!
    // e.g. If imageId = 1e0b6cce-6fb1-49bd-adac-53d43e151234
    // Then imageName = 1e0b6cce-6fb1-49bd-adac-53d43e151234.jpeg (or png, etc)
    @Schema(example = "1e0b6cce-6fb1-49bd-adac-53d43e151234")
    String imageId;

    @Schema(example = "1e0b6cce-6fb1-49bd-adac-53d43e151234.jpeg", type = "String", nullable = false)
    String imageName;

    @Schema(example = "testuser", nullable = false)
    String userId;

    @Schema(example = "2024-03-05T17:10:13")
    LocalDateTime timestamp;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request{
        @Schema(example = "1e0b6cce-6fb1-49bd-adac-53d43e151234", type = "String", nullable = false)
        String imageId;

        @Schema(example = "testuser", nullable = false)
        String userId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response{
        @Schema(example = "1e0b6cce-6fb1-49bd-adac-53d43e151234", type = "String", nullable = false)
        String imageId;

        @Schema(example = PLATFORM_SERVER_SOCKET_ADDR + IMAGE_CONTROLLER + "/1e0b6cce-6fb1-49bd-adac-53d43e151234.jpeg")
        String imageSrc;
    }

}
