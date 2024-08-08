package platform.dto.postdto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostingSearchDTO {

    @Schema(example = "testcategory")
    private String category;

    @Schema(example = "testsubcategory")
    private String subcategory;

    @Schema(example = "post")
    private String query;

    @Schema(example = "admin")
    private String userId;

}
