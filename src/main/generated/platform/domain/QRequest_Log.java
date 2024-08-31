package platform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRequest_Log is a Querydsl query type for Request_Log
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRequest_Log extends EntityPathBase<Request_Log> {

    private static final long serialVersionUID = 983044229L;

    public static final QRequest_Log request_Log = new QRequest_Log("request_Log");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath requestLog = createString("requestLog");

    public final DateTimePath<java.time.LocalDateTime> timestamp = createDateTime("timestamp", java.time.LocalDateTime.class);

    public final StringPath userId = createString("userId");

    public QRequest_Log(String variable) {
        super(Request_Log.class, forVariable(variable));
    }

    public QRequest_Log(Path<? extends Request_Log> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRequest_Log(PathMetadata metadata) {
        super(Request_Log.class, metadata);
    }

}

