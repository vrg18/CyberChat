package edu.vrg18.cyber_chat.controller;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.entity.Interlocutor;
import edu.vrg18.cyber_chat.entity.Message;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.service.InterlocutorService;
import edu.vrg18.cyber_chat.service.MessageService;
import edu.vrg18.cyber_chat.service.RoomService;
import edu.vrg18.cyber_chat.service.UserService;
import edu.vrg18.cyber_chat.utils.WebUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
public class ModeratorController {

    private final UserService userService;
    private final MessageService messageService;
    private final RoomService roomService;
    private final InterlocutorService interlocutorService;

    public ModeratorController(UserService userService, MessageService messageService, RoomService roomService, InterlocutorService interlocutorService) {
        this.userService = userService;
        this.messageService = messageService;
        this.roomService = roomService;
        this.interlocutorService = interlocutorService;
    }

    @GetMapping("/moderator")
    public String adminPage(Model model, Principal principal) {

        model.addAttribute("title", "ModeratorPage");

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        String userInfo = WebUtils.userToString(loginedUser);
        model.addAttribute("userInfo", userInfo);

        List<Message> messages = messageService.findAllMessages();
        model.addAttribute("messages", messages);
        List<Room> rooms = roomService.findAllRooms();
        model.addAttribute("rooms", rooms);
        List<Interlocutor> interlocutors = interlocutorService.findAllInterlocutors();
        model.addAttribute("interlocutors", interlocutors);

        return "moderation/moderatorPage";
    }

    @GetMapping("/edit_message/{id}")
    public String editMessage(@PathVariable UUID id, Model model) {

        model.addAttribute("title", "EditMessage");
        Message message = messageService.getMessageById(id).get();
        List<Room> rooms = roomService.findAllRooms();
        model.addAttribute("rooms", rooms);
        List<AppUser> users = userService.findAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("message", message);
        return "moderation/createOrEditMessage";
    }

    @PostMapping(value = "/save_message", params = "id!=")
    public String updateMessage(@ModelAttribute("message") Message message) {

        messageService.updateMessage(message);
        return "redirect:/moderator";
    }

    @GetMapping("/new_message")
    public String newMessage(Model model, Principal principal) {

        model.addAttribute("title", "NewMessage");
        List<Room> rooms = roomService.findAllRooms();
        model.addAttribute("rooms", rooms);
        List<AppUser> users = userService.findAllUsers();
        model.addAttribute("users", users);
        AppUser currentUser = userService.getUserByUserName(principal.getName()).get();
        model.addAttribute("currentUserId", currentUser.getId());
        model.addAttribute("newMessage", true);
        return "moderation/createOrEditMessage";
    }

    @PostMapping(value = "/save_message", params = "id=")
    public String createMessage(@ModelAttribute("message") Message message) {

        messageService.createMessage(message);
        return "redirect:/moderator";
    }

    @GetMapping("/delete_message/{id}")
    public String deleteMessage(@PathVariable UUID id) {

        messageService.deleteMessage(id);
        return "redirect:/moderator";
    }

    @GetMapping("/edit_room/{id}")
    public String editRoom(@PathVariable UUID id, Model model) {

        model.addAttribute("title", "EditRoom");
        Room room = roomService.getRoomById(id).get();
        List<AppUser> users = userService.findAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("room", room);
        return "moderation/createOrEditRoom";
    }

    @PostMapping(value = "/save_room", params = "id!=")
    public String updateRoom(@ModelAttribute("room") Room room) {

        roomService.updateRoom(room);
        return "redirect:/moderator";
    }

    @GetMapping("/new_room")
    public String newRoom(Model model, Principal principal) {

        model.addAttribute("title", "NewRoom");
        List<AppUser> users = userService.findAllUsers();
        model.addAttribute("users", users);
        AppUser currentUser = userService.getUserByUserName(principal.getName()).get();
        model.addAttribute("currentUserId", currentUser.getId());
        model.addAttribute("newRoom", true);
        return "moderation/createOrEditRoom";
    }

    @PostMapping(value = "/save_room", params = "id=")
    public String createRoom(@ModelAttribute("room") Room room) {

        roomService.createRoom(room);
        return "redirect:/moderator";
    }

    @GetMapping("/delete_room/{id}")
    public String deleteRoom(@PathVariable UUID id) {

        roomService.deleteRoom(id);
        return "redirect:/moderator";
    }

    @GetMapping("/edit_interlocutor/{id}")
    public String editInterlocutor(@PathVariable UUID id, Model model) {

        model.addAttribute("title", "EditInterlocutor");
        Interlocutor interlocutor = interlocutorService.getInterlocutorById(id).get();
        model.addAttribute("interlocutor", interlocutor);
        List<AppUser> users = userService.findAllUsers();
        model.addAttribute("users", users);
        List<Room> rooms = roomService.findAllRooms();
        model.addAttribute("rooms", rooms);
        return "moderation/createOrEditInterlocutor";
    }

    @PostMapping(value = "/save_interlocutor", params = "id!=")
    public String updateInterlocutor(@ModelAttribute("interlocutor") Interlocutor interlocutor) {

        interlocutorService.updateInterlocutor(interlocutor);
        return "redirect:/moderator";
    }

    @GetMapping("/new_interlocutor")
    public String newInterlocutor(Model model) {

        model.addAttribute("title", "NewInterlocutor");
        List<AppUser> users = userService.findAllUsers();
        model.addAttribute("users", users);
        List<Room> rooms = roomService.findAllRooms();
        model.addAttribute("rooms", rooms);
        model.addAttribute("newInterlocutor", true);
        return "moderation/createOrEditInterlocutor";
    }

    @PostMapping(value = "/save_interlocutor", params = "id=")
    public String createInterlocutor(@ModelAttribute("interlocutor") Interlocutor interlocutor) {

        interlocutorService.createInterlocutor(interlocutor);
        return "redirect:/moderator";
    }

    @GetMapping("/delete_interlocutor/{id}")
    public String deleteInterlocutor(@PathVariable UUID id) {

        interlocutorService.deleteInterlocutor(id);
        return "redirect:/moderator";
    }
}
