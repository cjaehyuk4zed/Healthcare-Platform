package platform.dto.postdto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static platform.constants.DirectoryMapConstants.PLATFORM_SERVER_SOCKET_ADDR;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostingImageDTO {

    @Schema(example = "1e0b6cce-6fb1-49bd-adac-53d43e151234.jpeg", type = "String", nullable = false)
    String imageName;

    @Schema(example = "testuser", nullable = false)
    String userId;

    @Schema(example = "3f2504e0-4f89-41d3-9a0c-0305e82c3301", type = "UUID", nullable = false)
    String postingId;

    @Schema(example = "2024-03-05T17:10:13")
    LocalDateTime dateCreated;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request{
        @Schema(example = "testuser", nullable = false)
        String userId;

        @Transient
        @Schema(example = "1e0b6cce-6fb1-49bd-adac-53d43e151234", type = "String", nullable = false)
        String imageId;

        @Schema(example = "3f2504e0-4f89-41d3-9a0c-0305e82c3301", type = "UUID", nullable = false)
        String postingId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        @Schema(example = PLATFORM_SERVER_SOCKET_ADDR + "/api/v2/images/3f2504e0-4f89-41d3-9a0c-0305e82c3301/name/1e0b6cce-6fb1-49bd-adac-53d43e151234.png")
        private String imageSrc;

        @Schema(example = "1e0b6cce-6fb1-49bd-adac-53d43e151234")
        private String imageId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Save{
        @Schema(example = "1e0b6cce-6fb1-49bd-adac-53d43e151234.jpeg", type = "String", nullable = true)
        String imageName;

        @Schema(example = "testuser", nullable = false)
        String userId;

        @Transient
        @Schema(example = "1e0b6cce-6fb1-49bd-adac-53d43e151234", type = "String", nullable = false)
        String imageId;

        @Schema(example = "3f2504e0-4f89-41d3-9a0c-0305e82c3301", type = "UUID", nullable = false)
        String postingId;
    }

}
