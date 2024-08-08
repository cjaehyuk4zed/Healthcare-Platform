package platform.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Posting_Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "posting_id")
    private UUID postingId;

    @Column(name = "posting_creator_id")
    private String postingCreatorId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "comment_content")
    private String commentContent;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "date_modified")
    private LocalDateTime dateModified;

    @Column(name = "likes", columnDefinition = "INT default 0")
    private int likes;

    @ManyToOne(targetEntity = Posting_Comment.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Posting_Comment parentComment;

    @Builder
    public Posting_Comment(String userId, UUID postingId, Long parentId, String commentContent, LocalDateTime timestamp, LocalDateTime dateModified, int likes){
        this.userId = userId;
        this.postingId = postingId;
        this.parentId = parentId;
        this.commentContent = commentContent;
        this.timestamp = timestamp;
        this.dateModified = dateModified;
        this.likes = likes;
    }
}
