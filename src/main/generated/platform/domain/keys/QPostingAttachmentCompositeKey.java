package platform.domain.keys;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPostingAttachmentCompositeKey is a Querydsl query type for PostingAttachmentCompositeKey
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QPostingAttachmentCompositeKey extends BeanPath<PostingAttachmentCompositeKey> {

    private static final long serialVersionUID = 2123701266L;

    public static final QPostingAttachmentCompositeKey postingAttachmentCompositeKey = new QPostingAttachmentCompositeKey("postingAttachmentCompositeKey");

    public final ComparablePath<java.util.UUID> attachmentId = createComparable("attachmentId", java.util.UUID.class);

    public final StringPath userId = createString("userId");

    public QPostingAttachmentCompositeKey(String variable) {
        super(PostingAttachmentCompositeKey.class, forVariable(variable));
    }

    public QPostingAttachmentCompositeKey(Path<? extends PostingAttachmentCompositeKey> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPostingAttachmentCompositeKey(PathMetadata metadata) {
        super(PostingAttachmentCompositeKey.class, metadata);
    }

}

