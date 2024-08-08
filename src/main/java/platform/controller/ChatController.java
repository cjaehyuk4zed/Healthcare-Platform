package platform.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import platform.constants.DirectoryMapConstants;
import platform.service.AuthService;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v2/chat")
@Tag(name = "ChatV2", description = "채팅 메신저 관련 APIs")
public class ChatController {

    // private class Chat is only used in ChatController.java to forward chat JSON data to the messenger application endpoint
    @Getter
    @Setter
    private class Chat{
        private String id;
        private String msg;
        private String sender; // 보내는 사람
        private String receiver; // 받는 사람
        private Long roomNum; // 방 번호
        private LocalDateTime createdAt;
    }

    private final AuthService authService;

    @GetMapping("/user/{userId}")
    public String getMsgBySenderAndReceiverRoomNum(@PathVariable(name = "userId") String userId){

        String principal = authService.getUserPrincipalOrThrow();

        String newEndpoint = "forward:/" + DirectoryMapConstants.MESSENGER_SERVER_SOCKET_ADDR + "/sender/" + principal + "/receiver/" + userId;

        return newEndpoint;
    }
}
