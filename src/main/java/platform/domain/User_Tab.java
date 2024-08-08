package platform.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import platform.domain.keys.UserTabCompositeKey;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User_Tab {

    // title, user_id
    @EmbeddedId
    private UserTabCompositeKey userTabCompositeKey;

    @Column(name = "tabId", columnDefinition = "binary(16) not null")
    private UUID tabId;

    // Cannot name as "index", as it is a reserved keyword in MySQL
    @Column(name = "tabIndex", columnDefinition = "INT")
    private int tabIndex;

    @Column(name = "timestamp", columnDefinition = "DATE")
    private LocalDate timestamp;

    @Column(name = "tab_content", columnDefinition = "LONGTEXT")
    private String tabContent;

    @MapsId("userId")
    @ManyToOne(targetEntity = User_Info.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User_Info userInfoFK;

}
