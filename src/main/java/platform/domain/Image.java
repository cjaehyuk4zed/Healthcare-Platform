package platform.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "image")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "image_name"))
public class Image {

    @Id
    @Column(name = "imageId", unique = true, columnDefinition = "BINARY(16) not NULL")
    private UUID imageId;

    // ImageName = imageId + File Extension!
    // e.g. If imageId = 1e0b6cce-6fb1-49bd-adac-53d43e151234
    // Then imageName = 1e0b6cce-6fb1-49bd-adac-53d43e151234.jpeg (or png, etc)
    @Column(name = "image_name", columnDefinition = "VARCHAR(63) not null")
    private String imageName;

    @Column(name = "user_id", columnDefinition = "VARCHAR(63) not null")
    private String userId;

    @Column(name = "is_saved", columnDefinition = "boolean not null default false")
    private boolean isSaved;

    @Column(name = "timestamp", columnDefinition = "DATETIME not null")
    LocalDateTime timestamp;

    @Builder
    public Image(String imageName, String userId, LocalDateTime timestamp){
        this.imageName = imageName;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    @Builder
    public Image(String imageName, String userId){
        this.imageName = imageName;
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
    }

    @MapsId("user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User_Info userInfo;

    public void modelMapperToUUID(String imageId) {
        this.imageId = UUID.fromString(imageId);
    }
}
