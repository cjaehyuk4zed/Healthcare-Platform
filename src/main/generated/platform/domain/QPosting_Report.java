package platform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPosting_Report is a Querydsl query type for Posting_Report
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPosting_Report extends EntityPathBase<Posting_Report> {

    private static final long serialVersionUID = 1558309952L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPosting_Report posting_Report = new QPosting_Report("posting_Report");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath postingCreatorId = createString("postingCreatorId");

    public final ComparablePath<java.util.UUID> postingId = createComparable("postingId", java.util.UUID.class);

    public final BooleanPath postingReported = createBoolean("postingReported");

    public final DateTimePath<java.time.LocalDateTime> timestamp = createDateTime("timestamp", java.time.LocalDateTime.class);

    public final StringPath userId = createString("userId");

    public final QUser_Info userInfo;

    public QPosting_Report(String variable) {
        this(Posting_Report.class, forVariable(variable), INITS);
    }

    public QPosting_Report(Path<? extends Posting_Report> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPosting_Report(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPosting_Report(PathMetadata metadata, PathInits inits) {
        this(Posting_Report.class, metadata, inits);
    }

    public QPosting_Report(Class<? extends Posting_Report> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userInfo = inits.isInitialized("userInfo") ? new QUser_Info(forProperty("userInfo"), inits.get("userInfo")) : null;
    }

}

