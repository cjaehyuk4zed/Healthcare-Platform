package platform.dto.postdto;

import jakarta.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostingInfoDTO {

    @Schema(example = "00000000-0000-0000-0000-000000000000", type = "UUID", nullable = false)
    private String postingId;

    @Schema(example = "admin", nullable = false)
    private String userId;

    @Schema(example = "Contents of User Post HTML generated by the text editor")
    private String postingContent;

    @Schema(example = "false", defaultValue = "false")
    private boolean postingSaved;

    @Schema(example = "false", defaultValue = "false")
    private boolean postingPublic;

    @Schema(example = "title")
    private String title;

    @Schema(example = "subtitle")
    private String subtitle;

    @Schema(example = "testcategory")
    private String category;

    @Schema(example = "testsubcategory")
    private String subcategory;

    @Nullable
    @Schema(example = "null", required = false)
    private LocalDateTime timestamp;

    @Nullable
    @Schema(example = "0")
    private Long comments;

    @Nullable
    @Schema(example = "0")
    private Long views;

    @Nullable
    @Schema(example = "0")
    private Long likes;

    @Nullable
    @Schema(example = "0", required = false)
    private Long faves;

    // Save name of thumbnail, and create logic to get the API for the thumbnail
    @Nullable
    private String thumbnail;

    // Approximately 'n minutes' to read this post
    @Nullable
    @Schema(example = "0")
    private Long readTime;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Save{

        @Schema(example = "00000000-0000-0000-0000-000000000000", type = "UUID", nullable = false)
        private String postingId;

        @Schema(example = "admin", nullable = false)
        private String userId;

        @Schema(example = "Contents of User Post HTML generated by the text editor")
        private String postingContent;

        @Schema(example = "true", defaultValue = "false")
        private boolean postingSaved;

        @Schema(example = "true", defaultValue = "false")
        private boolean postingPublic;

        @Schema(example = "title")
        private String title;

        @Schema(example = "subtitle")
        private String subtitle;

        @Schema(example = "testcategory")
        private String category;

        @Schema(example = "testsubcategory")
        private String subcategory;

        @Schema(example = "null")
        private LocalDateTime timestamp;

        @Nullable
        private String thumbnail;
    }

    //add fields as according to MySQL Posting_Info table

    // DTO에서는 String으로 통신하고, 저장단계에서 String으로 저장?
//    public Posting_Info toEntity() {
//        System.out.println("PostingInfoDTO UUID.fromString(postId) = " + UUID.fromString(postId));
//        return new Posting_Info(UUID.fromString(postId), userId, postContent, views, category, timestamp);
//    }
}
