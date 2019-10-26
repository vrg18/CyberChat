package edu.vrg18.cyber_chat.controller;

import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.service.InterlocutorService;
import edu.vrg18.cyber_chat.service.MessageService;
import edu.vrg18.cyber_chat.service.RoomService;
import edu.vrg18.cyber_chat.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR','ROLE_USER')")
public class ChatController {

    private final UserService userService;
    private final MessageService messageService;
    private final RoomService roomService;
    private final InterlocutorService interlocutorService;

    public ChatController(UserService userService, MessageService messageService, RoomService roomService, InterlocutorService interlocutorService) {
        this.userService = userService;
        this.messageService = messageService;
        this.roomService = roomService;
        this.interlocutorService = interlocutorService;
    }

    @GetMapping("/")
    public String welcomePage(Model model) {
        model.addAttribute("title", "CyberChat");
        model.addAttribute("message", "Добро пожаловать в CyberChat!");
        List<Room> rooms = roomService.findAllRooms();
        model.addAttribute("rooms", rooms);
        return "chatPage";
    }
}
