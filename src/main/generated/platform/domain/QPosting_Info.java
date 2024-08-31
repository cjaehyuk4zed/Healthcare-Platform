package platform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPosting_Info is a Querydsl query type for Posting_Info
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPosting_Info extends EntityPathBase<Posting_Info> {

    private static final long serialVersionUID = -1357295942L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPosting_Info posting_Info = new QPosting_Info("posting_Info");

    public final StringPath category = createString("category");

    public final QCategory categoryFK;

    public final NumberPath<Long> comments = createNumber("comments", Long.class);

    public final NumberPath<Long> faves = createNumber("faves", Long.class);

    public final NumberPath<Long> likes = createNumber("likes", Long.class);

    public final StringPath postingContent = createString("postingContent");

    public final ComparablePath<java.util.UUID> postingId = createComparable("postingId", java.util.UUID.class);

    public final BooleanPath postingPublic = createBoolean("postingPublic");

    public final BooleanPath postingSaved = createBoolean("postingSaved");

    public final NumberPath<Integer> readTime = createNumber("readTime", Integer.class);

    public final StringPath subcategory = createString("subcategory");

    public final QSubcategory subcategoryFK;

    public final StringPath subtitle = createString("subtitle");

    public final StringPath thumbnail = createString("thumbnail");

    public final DateTimePath<java.time.LocalDateTime> timestamp = createDateTime("timestamp", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    public final StringPath userId = createString("userId");

    public final QUser_Info userInfo;

    public final NumberPath<Long> views = createNumber("views", Long.class);

    public QPosting_Info(String variable) {
        this(Posting_Info.class, forVariable(variable), INITS);
    }

    public QPosting_Info(Path<? extends Posting_Info> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPosting_Info(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPosting_Info(PathMetadata metadata, PathInits inits) {
        this(Posting_Info.class, metadata, inits);
    }

    public QPosting_Info(Class<? extends Posting_Info> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.categoryFK = inits.isInitialized("categoryFK") ? new QCategory(forProperty("categoryFK")) : null;
        this.subcategoryFK = inits.isInitialized("subcategoryFK") ? new QSubcategory(forProperty("subcategoryFK"), inits.get("subcategoryFK")) : null;
        this.userInfo = inits.isInitialized("userInfo") ? new QUser_Info(forProperty("userInfo"), inits.get("userInfo")) : null;
    }

}

