package platform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPosting_Attachment is a Querydsl query type for Posting_Attachment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPosting_Attachment extends EntityPathBase<Posting_Attachment> {

    private static final long serialVersionUID = 496142895L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPosting_Attachment posting_Attachment = new QPosting_Attachment("posting_Attachment");

    public final StringPath attachmentName = createString("attachmentName");

    public final platform.domain.keys.QPostingAttachmentCompositeKey postingAttachmentCompositeKey;

    public final ComparablePath<java.util.UUID> postingId = createComparable("postingId", java.util.UUID.class);

    public final DateTimePath<java.time.LocalDateTime> timestamp = createDateTime("timestamp", java.time.LocalDateTime.class);

    public final QUser_Info userInfo;

    public QPosting_Attachment(String variable) {
        this(Posting_Attachment.class, forVariable(variable), INITS);
    }

    public QPosting_Attachment(Path<? extends Posting_Attachment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPosting_Attachment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPosting_Attachment(PathMetadata metadata, PathInits inits) {
        this(Posting_Attachment.class, metadata, inits);
    }

    public QPosting_Attachment(Class<? extends Posting_Attachment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.postingAttachmentCompositeKey = inits.isInitialized("postingAttachmentCompositeKey") ? new platform.domain.keys.QPostingAttachmentCompositeKey(forProperty("postingAttachmentCompositeKey")) : null;
        this.userInfo = inits.isInitialized("userInfo") ? new QUser_Info(forProperty("userInfo"), inits.get("userInfo")) : null;
    }

}

