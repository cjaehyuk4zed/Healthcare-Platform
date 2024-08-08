package platform.dto.postdto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
//@NoArgsConstructor
public class PostingCommentDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        @Schema(example = "Add a comment")
        private String commentContent;
        @Nullable
        @Schema(example = "0")
        private Long parentId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Edit {
        @Schema(example = "Add a comment")
        private String commentContent;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response{
        private Long commentId;
        private String userId;
        private UUID postingId;
        @Nullable
        private Long parentId;
        private String commentContent;
        private String dateCreated;
        private String dateModified;
        private int likes;

        public void formatDateCreated(LocalDateTime time){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            this.dateCreated = time.format(formatter);
        }

        public void setDateModifiedFormatted(LocalDateTime dateViewedRaw){
            Duration duration = Duration.between(dateViewedRaw, LocalDateTime.now());
            long days = duration.toDays();
            long hours = duration.toHours() % 24;
            long minutes = duration.toMinutes() % 60;
            if(days > 0 ){this.dateModified = "Last modified : " + days + "d ago";}
            else if(hours > 0){this.dateModified =  "Last modified : " + hours + "h ago";}
            else if(minutes > 0){this.dateModified =  "Last modified : " + minutes + "m ago";}
            else {this.dateModified =  "Last modified : " + duration.toSeconds() + "s ago";}
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Save {
        private String userId;
        private UUID postingId;
        private Long parentId;
        private String commentContent;
        private LocalDateTime dateCreated;
        private LocalDateTime dateModified;
        private int likes;
    }
}
