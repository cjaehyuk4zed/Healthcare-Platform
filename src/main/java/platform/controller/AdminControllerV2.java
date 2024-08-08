package platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import platform.service.AdminService;
import platform.service.PostingInfoService;

import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin")
@Tag(name = "Admin", description = "Admin 권한 전용 API")
public class AdminControllerV2 {

    private final PostingInfoService postingInfoService;
    private final AdminService adminService;

    @ApiResponse(responseCode = "200", description = "임시저장 게시글 삭제. 임시저장 게시글이 없을 경우 아무일도 일어나지 않음.")
    @Operation(summary = "임시저장 게시글들 삭제", description = "임시저장 상태인 게시글 및 해당 게시글들의 이미지, 첨부파일 삭제")
    @DeleteMapping("/delete/drafts")
    public ResponseEntity<String> deleteDrafts() {
        try {
            log.info("AdminControllerV2 /delete/drafts");
            boolean delete = postingInfoService.deleteDrafts();
            if(delete){
                return ResponseEntity.ok().body("Drafts deleted successfully");
            }
            else {
                return ResponseEntity.ok().body("No drafts were found to be deleted");
            }
        } catch (IOException e) {
            log.info("AdminControllerV2 /delete/drafts : {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Drafts deleted successfully");
        }
    }

    @Operation(summary = "사용자 권한 변경", description = "관리자 권한이 있는 계정이 다른 사용자의 계정 권한을 변경")
    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<String> setAuthority(@PathVariable(name = "userId") String userId,
                                               @RequestParam(name = "role") String role){

        return ResponseEntity.ok().body("");
    }

    @Operation(summary = "상위 카테고리 추가", description = "상위 카테고리 추가 : category 이름을 지정하면 된다.")
    @PostMapping("/category")
    public ResponseEntity<String> addNewCategory(@RequestPart(name = "category") String category){
        adminService.addNewCategory(category);
        return ResponseEntity.ok("Added new category : " + category);
    }

    @Operation(summary = "하위 카테고리 추가", description = "하위 카테고리 추가 : 상위 category 이름을 지정하고, 그 하위에 들어갈 subcategory의 이름 지정.")
    @PostMapping("/subcategory")
    public ResponseEntity<String> addNewSubcategory(@RequestPart(name = "category") String category,
                                                    @RequestPart(name = "subcategory") String subcategory){
        adminService.addNewSubcategory(category, subcategory);
        return ResponseEntity.ok("Added new subcategory : + " + subcategory + " | within category : " + category);
    }


}
