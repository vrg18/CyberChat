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

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
public class ModeratorController {

    private final UserService userService;
    private final MessageService messageService;
    private final RoomService roomService;
    private final InterlocutorService interlocutorService;

    public ModeratorController(UserService userService, MessageService messageService,
                               RoomService roomService, InterlocutorService interlocutorService) {
        this.userService = userService;
        this.messageService = messageService;
        this.roomService = roomService;
        this.interlocutorService = interlocutorService;
    }

    @GetMapping("/moderator")
    public String adminPage(Model model, Principal principal) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        String userInfo = WebUtils.userToString(loginedUser);
        model.addAttribute("userInfo", userInfo);

        List<Message> messages = messageService.findAllMessages(false);
        model.addAttribute("messages", messages);

        List<Room> rooms = roomService.findAllRooms();
        model.addAttribute("rooms", rooms);

        List<Interlocutor> interlocutors = interlocutorService.findAllInterlocutors();
        model.addAttribute("interlocutors", interlocutors);
        model.addAttribute("interlocutorService", interlocutorService);

        model.addAttribute("title", "ModeratorPage");
        return "moderation/moderatorPage";
    }

    @GetMapping("/edit_message/{id}")
    public String editMessage(@PathVariable UUID id, Model model) {

        Message message = messageService.getMessageById(id).get();
        model.addAttribute("message", message);

        List<Room> rooms = roomService.findAllRooms();
        model.addAttribute("rooms", rooms);

        List<AppUser> users = userService.findAllUsers();
        model.addAttribute("users", users);

        model.addAttribute("title", "EditMessage");
        return "moderation/createOrEditMessage";
    }

    @PostMapping(value = "/save_message", params = "id!=")
    public String updateMessage(@ModelAttribute("message") Message message) {

        messageService.updateMessage(message);
        return "redirect:/moderator";
    }

    @GetMapping("/new_message")
    public String newMessage(Model model, Principal principal) {

        List<Room> rooms = roomService.findAllRooms();
        model.addAttribute("rooms", rooms);

        List<AppUser> users = userService.findAllUsers();
        model.addAttribute("users", users);

        AppUser currentUser = userService.getUserByUserName(principal.getName()).get();
        model.addAttribute("currentUserId", currentUser.getId());

        model.addAttribute("newMessage", true);
        model.addAttribute("title", "NewMessage");
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

        Room room = roomService.getRoomById(id).get();
        model.addAttribute("room", room);

        List<AppUser> users = userService.findAllUsers();
        model.addAttribute("users", users);

        model.addAttribute("title", "EditRoom");
        return "moderation/createOrEditRoom";
    }

    @PostMapping(value = "/save_room", params = "id!=")
    public String updateRoom(@ModelAttribute("room") Room room) {

        roomService.updateRoom(room);
        return "redirect:/moderator";
    }

    @GetMapping("/new_room")
    public String newRoom(Model model, Principal principal) {

        List<AppUser> users = userService.findAllUsers();
        model.addAttribute("users", users);

        AppUser currentUser = userService.getUserByUserName(principal.getName()).get();
        model.addAttribute("currentUserId", currentUser.getId());

        model.addAttribute("newRoom", true);
        model.addAttribute("title", "NewRoom");
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

        Interlocutor interlocutor = interlocutorService.getInterlocutorById(id).get();
        model.addAttribute("interlocutor", interlocutor);

        List<AppUser> users = userService.findAllUsers();
        model.addAttribute("users", users);

        List<Room> rooms = roomService.findAllRooms();
        model.addAttribute("rooms", rooms);

        model.addAttribute("title", "EditInterlocutor");
        return "moderation/createOrEditInterlocutor";
    }

    @PostMapping(value = "/save_interlocutor", params = "id!=")
    public String updateInterlocutor(@ModelAttribute("interlocutor") Interlocutor interlocutor) {

        interlocutorService.updateInterlocutor(interlocutor);
        return "redirect:/moderator";
    }

    @GetMapping("/new_interlocutor")
    public String newInterlocutor(Model model) {

        List<AppUser> users = userService.findAllUsers();
        model.addAttribute("users", users);

        List<Room> rooms = roomService.findAllRooms();
        model.addAttribute("rooms", rooms);

        model.addAttribute("newInterlocutor", true);
        model.addAttribute("title", "NewInterlocutor");
        return "moderation/createOrEditInterlocutor";
    }

    @PostMapping(value = "/save_interlocutor", params = "id=")
    public String createInterlocutor(@ModelAttribute("interlocutor") Interlocutor interlocutor) {

        interlocutorService.createInterlocutor(interlocutor);
        return "redirect:/moderator";
    }

    @GetMapping("/delete_interlocutor/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_USER')")
    public String deleteInterlocutor(@PathVariable UUID id, HttpServletRequest request) {

        interlocutorService.deleteInterlocutor(id);
        String referer = request.getHeader("Referer");
        return "redirect:".concat(referer);
    }

    @GetMapping("/edit_list_room/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_USER')")
    public String editAndListRoom(@PathVariable UUID id, Model model) {

        Room room = roomService.getRoomById(id).get();
        model.addAttribute("room", room);

        List<AppUser> users = userService.findAllUsers();
        model.addAttribute("users", users);

        List<Interlocutor> interlocutors = interlocutorService.findAllInterlocutorsInRoomId(id);
        model.addAttribute("interlocutors", interlocutors);

        model.addAttribute("title", "EditAndListRoom");
        return "moderation/editAndListRoom";
    }

    @PostMapping("/update_room")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_USER')")
    public String updateRoom2(@ModelAttribute("room") Room room) {

        roomService.updateRoom(room);
        return "redirect:/";
    }

    @GetMapping("/new_interlocutor_room/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_USER')")
    public String newInterlocutorInRoom(@PathVariable UUID id, Model model) {

        List<AppUser> users = userService.findAllUsers();
        model.addAttribute("users", users);

        Room room = roomService.getRoomById(id).get();
        model.addAttribute("room", room);

        model.addAttribute("title", "NewInterlocutorInRoom");
        return "moderation/addInterlocutorInRoom";
    }

    @PostMapping("/add_interlocutor_room")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_USER')")
    public String addInterlocutorInRoom(@ModelAttribute("interlocutor") Interlocutor interlocutor) {

        interlocutorService.createInterlocutor(interlocutor);
        return "redirect:/edit_list_room/".concat(interlocutor.getRoom().getId().toString());
    }
}
