package platform.domain.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class PostingImageCompositeKey implements Serializable {
    @Column(name = "user_id", columnDefinition = "VARCHAR(63)")
    private String userId;

    @Column(name = "image_name", columnDefinition = "VARCHAR(63)")
    private String imageName;
}
