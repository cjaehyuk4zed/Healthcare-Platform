package platform.domain.keys;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserInterestCompositeKey is a Querydsl query type for UserInterestCompositeKey
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUserInterestCompositeKey extends BeanPath<UserInterestCompositeKey> {

    private static final long serialVersionUID = 1877295832L;

    public static final QUserInterestCompositeKey userInterestCompositeKey = new QUserInterestCompositeKey("userInterestCompositeKey");

    public final StringPath category = createString("category");

    public final StringPath subcategory = createString("subcategory");

    public final StringPath userId = createString("userId");

    public QUserInterestCompositeKey(String variable) {
        super(UserInterestCompositeKey.class, forVariable(variable));
    }

    public QUserInterestCompositeKey(Path<? extends UserInterestCompositeKey> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserInterestCompositeKey(PathMetadata metadata) {
        super(UserInterestCompositeKey.class, metadata);
    }

}

