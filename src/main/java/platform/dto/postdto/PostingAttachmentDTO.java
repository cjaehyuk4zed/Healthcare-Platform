package platform.dto.postdto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

import static platform.constants.DirectoryMapConstants.FILE_CONTROLLER;
import static platform.constants.DirectoryMapConstants.PLATFORM_SERVER_SOCKET_ADDR;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostingAttachmentDTO {
    String attachmentName;

    @Schema(example = "2f5c0646-b823-4259-a079-f8bf73d0e952")
    String attachmentId;

    @Schema(example = "admin")
    String userId;

    @Schema(example = "00000000-0000-0000-0000-000000000000")
    String postingId;

    LocalDateTime timestamp;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request{
        @Schema(example = "admin")
        String userId;

        @Schema(example = "00000000-0000-0000-0000-000000000000")
        String postingId;

        @Schema(example = "2f5c0646-b823-4259-a079-f8bf73d0e952")
        String attachmentId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response{
        @Schema(example = "testdoc.pdf")
        String attachmentName;

        @Schema(example = "2f5c0646-b823-4259-a079-f8bf73d0e952")
        String attachmentId;

        @Schema(example = "admin")
        String userId;

        @Schema(example = "00000000-0000-0000-0000-000000000000")
        String postingId;

        @Schema(example = "2024-03-05T17:10:13")
        LocalDateTime dateCreated;

        @Schema(example = PLATFORM_SERVER_SOCKET_ADDR + FILE_CONTROLLER + "/users/admin/posts/00000000-0000-0000-0000-000000000000/attachments/2f5c0646-b823-4259-a079-f8bf73d0e952")
        String attachmentSrc;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SrcLink {
        @Schema(example = PLATFORM_SERVER_SOCKET_ADDR + FILE_CONTROLLER + "/users/admin/posts/00000000-0000-0000-0000-000000000000/attachments/2f5c0646-b823-4259-a079-f8bf73d0e952")
        private String attachmentSrc;

        @Schema(example = "2f5c0646-b823-4259-a079-f8bf73d0e952")
        private String attachmentId;
    }

}
