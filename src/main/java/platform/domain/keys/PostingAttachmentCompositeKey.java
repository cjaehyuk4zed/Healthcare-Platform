package platform.domain.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class PostingAttachmentCompositeKey implements Serializable {
    @Column(name = "attachment_id")
    private UUID attachmentId;

    @Column(name = "user_id", columnDefinition = "VARCHAR(63) not null")
    private String userId;

    // Refactor this code if possible!
    public void modelMapperToUUID(String attachmentId) {
        this.attachmentId = UUID.fromString(attachmentId);
    }

    public String modelMapperToString(){
        return this.attachmentId.toString();
    }
}
