package platform.dto.userdto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserTabDTO {

    @Schema(example = "admin")
    private String userId;

    @Schema(example = "tab_name")
    private String title;

    @Schema(example = "00000000-0000-0000-0000-000000000000", type = "UUID", nullable = false)
    private UUID tabId;

    @Schema(example = "1")
    private int tabIndex;

    @Schema(example = "2024-01-01")
    private LocalDate timestamp;

    @Schema(example = "This is the tab contents")
    private String tabContent;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response {
        @Schema(example = "admin")
        private String userId;

        @Schema(example = "tab_name")
        private String title;

        @Schema(example = "00000000-0000-0000-0000-000000000000", type = "UUID", nullable = false)
        private UUID tabId;

        @Schema(example = "2024-01-01")
        private String timestamp;

        @Schema(example = "This is the tab contents")
        private String tabContent;

        public void formatTimestamp(LocalDate time){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            this.timestamp = time.format(formatter);
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Header{
        @Schema(example = "tab_name")
        private String title;

        @Schema(example = "00000000-0000-0000-0000-000000000000", type = "UUID", nullable = false)
        private String tabId;
    }
}
