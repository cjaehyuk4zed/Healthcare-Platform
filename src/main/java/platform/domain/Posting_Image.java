package platform.domain;

import lombok.*;
import platform.domain.keys.PostingImageCompositeKey;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

// Obsolete class, no longer being used
// Has been left for old DB data purposes
// Delete class when no longer needed
@Entity(name = "posting_image")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "image_name"))
public class Posting_Image {

    // imageName and userId
    @EmbeddedId
    private PostingImageCompositeKey postingImageCompositeKey;

    @Column(name = "posting_id")
    UUID postingId;

    @Column(name = "timestamp", columnDefinition = "DATETIME not null")
    LocalDateTime timestamp;

    @MapsId("user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User_Info userInfo;
}
