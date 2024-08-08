package platform.dto.postdto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostingDraftDTO {
    @Schema(example = "00000000-0000-0000-0000-000000000000", type = "UUID", nullable = false)
    private String postingId;

    @Schema(example = "admin", nullable = false)
    private String userId;

    @Schema(example = "Contents of User Post HTML generated by the text editor")
    private String postingContent;

    @Schema(example = "title")
    private String title;

    @Schema(example = "subtitle")
    private String subtitle;

    @Schema(example = "testcategory")
    private String category;

    @Schema(example = "testsubcategory")
    private String subcategory;

    // Save name of thumbnail, and create logic to get the API for the thumbnail
    @Nullable
    private String thumbnail;
}
