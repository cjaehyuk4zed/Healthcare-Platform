package platform.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

// `Post` name may be a predefined variable in many programs, so the name "post_info" was chosen to avoid errors
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "post_id"))
public class Posting_Info {
    @Id
    @Column(name = "posting_id", unique = true, columnDefinition = "BINARY(16) not NULL")
    private UUID postingId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "posting_content", columnDefinition = "LONGTEXT")
    private String postingContent;

    @Column(name = "posting_saved", columnDefinition = "BOOLEAN default FALSE")
    private boolean postingSaved;

    @Column(name = "posting_public", columnDefinition = "BOOLEAN default FALSE")
    private boolean postingPublic;

    @Column(name = "title")
    private String title;

    @Column(name = "subtitle")
    private String subtitle;

    @Column(name = "category")
    private String category;

    @Column(name = "subcategory")
    private String subcategory;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "comments", columnDefinition = "INT default 0")
    private Long comments;

    @Column(name = "views", columnDefinition = "INT default 0")
    private Long views;

    @Column(name = "likes", columnDefinition = "INT default 0")
    private Long likes;

    @Column(name = "faves", columnDefinition = "INT default 0")
    private Long faves;

    @Column(name = "thumbnail")
    private String thumbnail; // Save the name of the thumbnail file

    @Column(name = "read_time", columnDefinition = "INT default 1")
    private int readTime;

    @MapsId("userId")
    @ManyToOne(targetEntity = User_Info.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User_Info userInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category", referencedColumnName = "category", insertable = false, updatable = false)
    private Category categoryFK;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory", referencedColumnName = "subcategory", insertable = false, updatable = false)
    private Subcategory subcategoryFK;
}
