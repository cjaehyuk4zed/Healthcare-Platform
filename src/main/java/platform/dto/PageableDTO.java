package platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageableDTO {
    @Schema(example = "0")
    private int page;

    @Schema(example = "10")
    private int size;

    @Schema(example = "recent")
    private String sort;

    @Schema(example = "desc")
    private String sortOrder;
}
