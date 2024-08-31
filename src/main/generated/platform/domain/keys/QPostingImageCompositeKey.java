package platform.domain.keys;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPostingImageCompositeKey is a Querydsl query type for PostingImageCompositeKey
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QPostingImageCompositeKey extends BeanPath<PostingImageCompositeKey> {

    private static final long serialVersionUID = -1770166276L;

    public static final QPostingImageCompositeKey postingImageCompositeKey = new QPostingImageCompositeKey("postingImageCompositeKey");

    public final StringPath imageName = createString("imageName");

    public final StringPath userId = createString("userId");

    public QPostingImageCompositeKey(String variable) {
        super(PostingImageCompositeKey.class, forVariable(variable));
    }

    public QPostingImageCompositeKey(Path<? extends PostingImageCompositeKey> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPostingImageCompositeKey(PathMetadata metadata) {
        super(PostingImageCompositeKey.class, metadata);
    }

}

