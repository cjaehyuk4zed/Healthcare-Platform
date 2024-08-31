package platform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPosting_Comment is a Querydsl query type for Posting_Comment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPosting_Comment extends EntityPathBase<Posting_Comment> {

    private static final long serialVersionUID = 918763731L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPosting_Comment posting_Comment = new QPosting_Comment("posting_Comment");

    public final StringPath commentContent = createString("commentContent");

    public final DateTimePath<java.time.LocalDateTime> dateModified = createDateTime("dateModified", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> likes = createNumber("likes", Integer.class);

    public final QPosting_Comment parentComment;

    public final NumberPath<Long> parentId = createNumber("parentId", Long.class);

    public final StringPath postingCreatorId = createString("postingCreatorId");

    public final ComparablePath<java.util.UUID> postingId = createComparable("postingId", java.util.UUID.class);

    public final DateTimePath<java.time.LocalDateTime> timestamp = createDateTime("timestamp", java.time.LocalDateTime.class);

    public final StringPath userId = createString("userId");

    public QPosting_Comment(String variable) {
        this(Posting_Comment.class, forVariable(variable), INITS);
    }

    public QPosting_Comment(Path<? extends Posting_Comment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPosting_Comment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPosting_Comment(PathMetadata metadata, PathInits inits) {
        this(Posting_Comment.class, metadata, inits);
    }

    public QPosting_Comment(Class<? extends Posting_Comment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.parentComment = inits.isInitialized("parentComment") ? new QPosting_Comment(forProperty("parentComment"), inits.get("parentComment")) : null;
    }

}

