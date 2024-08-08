package platform.dto.postdto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostingPreviewDTO {
    @Schema(example = "00000000-0000-0000-0000-000000000000", type = "UUID", nullable = false)
    private String postingId;

    @Schema(example = "admin", nullable = false)
    private String userId;

    @Schema(example = "title")
    private String title;

    @Schema(example = "subtitle")
    private String subtitle;

    @Schema(example = "testcategory")
    private String category;

    @Schema(example = "testsubcategory")
    private String subcategory;

    @Schema(example = "null")
    private String dateCreated;

    @Schema(example = "null")
    private String dateViewed;

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
    private String thumbnail;

    public void formatDateCreated(LocalDateTime time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.dateCreated = time.format(formatter);
    }
//
//    public void setDateViewedFormatted(LocalDateTime dateViewedRaw){
//        Duration duration = Duration.between(dateViewedRaw, LocalDateTime.now());
//        long days = duration.toDays();
//        long hours = duration.toHours() % 24;
//        long minutes = duration.toMinutes() % 60;
//        if(days > 0 ){this.dateViewed = "Last viewed : " + days + "d ago";}
//        else if(hours > 0){this.dateViewed =  "Last viewed : " + hours + "h ago";}
//        else if(minutes > 0){this.dateViewed =  "Last viewed : s" + minutes + "m ago";}
//        else {this.dateViewed =  "Last Viewed : " + duration.toSeconds() + "s ago";}
//    }
}
