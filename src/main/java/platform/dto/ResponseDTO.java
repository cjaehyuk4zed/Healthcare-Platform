package platform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// This class is to be used ONLY WHEN NECESSARY
// Used as the ResponseEntity body when multiple DTOs data need to be returned together
public class ResponseDTO<K> {

    private List<K> DTOs;

}
