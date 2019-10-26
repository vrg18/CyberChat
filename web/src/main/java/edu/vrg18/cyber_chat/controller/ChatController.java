package edu.vrg18.cyber_chat.controller;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.entity.Message;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.service.InterlocutorService;
import edu.vrg18.cyber_chat.service.MessageService;
import edu.vrg18.cyber_chat.service.RoomService;
import edu.vrg18.cyber_chat.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

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
    public String chatPage(Model model, Principal principal) {
        AppUser currentUser = userService.getUserByUserName(principal.getName()).get();
        String lastRoomId = currentUser.getLastRoom().getId().toString();
        return "redirect:/room/".concat(lastRoomId);
    }

    @GetMapping("/room/{id}")
    public String roomPage(@PathVariable UUID id, Model model, Principal principal) {
        model.addAttribute("title", "CyberChat");
        model.addAttribute("message", "Добро пожаловать в CyberChat!");
        AppUser currentUser = userService.getUserByUserName(principal.getName()).get();
        List<Room> rooms = roomService.findAllRoomsByUser(currentUser);
        model.addAttribute("rooms", rooms);
        List<Message> messages = messageService.findAllMessages();
        model.addAttribute("messages", messages);
        List<AppUser> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "roomPage";
    }
}
