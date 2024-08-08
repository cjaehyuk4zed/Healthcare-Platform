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
public class UserTabCompositeKey implements Serializable {
    @Column(name = "title", columnDefinition = "VARCHAR(63) not NULL")
    private String title;

    @Column(name = "user_id", unique = true, columnDefinition = "VARCHAR(63)")
    private String userId;

}
