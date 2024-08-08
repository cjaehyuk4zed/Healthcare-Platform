package platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import platform.service.HeaderService;

import java.util.List;

@Controller
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/v2/header")
@Tag(name = "HeaderV2", description = "페이지 헤더 관련 API")
public class HeaderControllerV2 {

    private final HeaderService headerService;

    @Operation(summary = "카테고리 목록 조회", description = "배열의 배열을 반환. 각 배열의 첫 원소가 상위category, 그 아래에 원소들이 subcategory이다. 상위 카테고리 오름차순으로 나열.")
    @GetMapping("/categories")
    public ResponseEntity<List<List<String>>> getAllCategories(){
        List<List<String>> listList = headerService.getAllCategories();
        return ResponseEntity.ok(listList);
    }

    @Operation(summary = "특정 상위 카테고리의 하위 카테고리 목록 조회", description = "해당 상위category의 하위 카테고리 목록만 반환")
    @GetMapping("/category/{category}/subcategories")
    public ResponseEntity<List<String>> getAllSubcategories(@PathVariable(name = "category") String category){
        List<String> list = headerService.getAllSubcategories(category);
        return ResponseEntity.ok(list);
    }
}
