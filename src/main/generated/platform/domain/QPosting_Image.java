package platform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPosting_Image is a Querydsl query type for Posting_Image
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPosting_Image extends EntityPathBase<Posting_Image> {

    private static final long serialVersionUID = 873464015L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPosting_Image posting_Image = new QPosting_Image("posting_Image");

    public final ComparablePath<java.util.UUID> postingId = createComparable("postingId", java.util.UUID.class);

    public final platform.domain.keys.QPostingImageCompositeKey postingImageCompositeKey;

    public final DateTimePath<java.time.LocalDateTime> timestamp = createDateTime("timestamp", java.time.LocalDateTime.class);

    public final QUser_Info userInfo;

    public QPosting_Image(String variable) {
        this(Posting_Image.class, forVariable(variable), INITS);
    }

    public QPosting_Image(Path<? extends Posting_Image> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPosting_Image(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPosting_Image(PathMetadata metadata, PathInits inits) {
        this(Posting_Image.class, metadata, inits);
    }

    public QPosting_Image(Class<? extends Posting_Image> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.postingImageCompositeKey = inits.isInitialized("postingImageCompositeKey") ? new platform.domain.keys.QPostingImageCompositeKey(forProperty("postingImageCompositeKey")) : null;
        this.userInfo = inits.isInitialized("userInfo") ? new QUser_Info(forProperty("userInfo"), inits.get("userInfo")) : null;
    }

}

