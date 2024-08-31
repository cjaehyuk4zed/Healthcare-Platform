package platform.domain.keys;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserTabCompositeKey is a Querydsl query type for UserTabCompositeKey
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUserTabCompositeKey extends BeanPath<UserTabCompositeKey> {

    private static final long serialVersionUID = -366018409L;

    public static final QUserTabCompositeKey userTabCompositeKey = new QUserTabCompositeKey("userTabCompositeKey");

    public final StringPath title = createString("title");

    public final StringPath userId = createString("userId");

    public QUserTabCompositeKey(String variable) {
        super(UserTabCompositeKey.class, forVariable(variable));
    }

    public QUserTabCompositeKey(Path<? extends UserTabCompositeKey> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserTabCompositeKey(PathMetadata metadata) {
        super(UserTabCompositeKey.class, metadata);
    }

}

