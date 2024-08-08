package platform.domain;

import jakarta.persistence.*;
import lombok.*;
import platform.domain.keys.PostingAttachmentCompositeKey;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "attachment_id"))
public class Posting_Attachment {
    @Column(name = "attachment_name")
    String attachmentName;

    @EmbeddedId
    private PostingAttachmentCompositeKey postingAttachmentCompositeKey;

    @Column(name = "posting_id")
    UUID postingId;

    @Column(name = "timestamp")
    LocalDateTime timestamp;

    @MapsId("user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", columnDefinition = "VARCHAR(63) not null")
    private User_Info userInfo;
}
