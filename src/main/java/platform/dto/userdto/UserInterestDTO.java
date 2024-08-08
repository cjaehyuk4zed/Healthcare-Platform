package platform.dto.userdto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInterestDTO {
    @Schema(example = "admin")
    private String userId;

    @Schema(example = "testcategory")
    private String category;

    @Schema(example = "testsubcategory")
    private String subcategory;

    @Schema(example = "false")
    private Boolean userInterested;

    @Schema(example = "1")
    private int likes;

    private LocalDateTime timestamp;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Save{
        @Schema(example = "admin")
        private String userId;

        @Schema(example = "testcategory")
        private String category;

        @Schema(example = "testsubcategory")
        private String subcategory;

        @Schema(example = "false")
        private Boolean userInterested;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompositeKey{
        @Schema(example = "admin")
        private String userId;

        @Schema(example = "testcategory")
        private String category;

        @Schema(example = "testsubcategory")
        private String subcategory;

    }
}
