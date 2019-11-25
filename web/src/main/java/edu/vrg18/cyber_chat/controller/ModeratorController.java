package edu.vrg18.cyber_chat.controller;

import edu.vrg18.cyber_chat.dto.InterlocutorDto;
import edu.vrg18.cyber_chat.dto.MessageDto;
import edu.vrg18.cyber_chat.dto.RoomDto;
import edu.vrg18.cyber_chat.dto.UserDto;
import edu.vrg18.cyber_chat.service.InterlocutorService;
import edu.vrg18.cyber_chat.service.MessageService;
import edu.vrg18.cyber_chat.service.RoomService;
import edu.vrg18.cyber_chat.service.UserService;
import edu.vrg18.cyber_chat.util.PaginationAssistant;
import edu.vrg18.cyber_chat.utils.WebUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    public String adminPage(Model model, Principal principal,
                            @RequestParam("rPage") Optional<Integer> rPage,
                            @RequestParam("rSize") Optional<Integer> rSize,
                            @RequestParam("mPage") Optional<Integer> mPage,
                            @RequestParam("mSize") Optional<Integer> mSize) {

        int rCurrentPage = rPage.orElse(1);
        int rPageSize = rSize.orElse(5);
        int mCurrentPage = mPage.orElse(1);
        int mPageSize = mSize.orElse(8);

        org.springframework.security.core.userdetails.User loginedUser =
                (org.springframework.security.core.userdetails.User) ((Authentication) principal).getPrincipal();
        String userInfo = WebUtils.userToString(loginedUser);
        model.addAttribute("userInfo", userInfo);

        PaginationAssistant.assistant("room", model,
                roomService.findAllRooms(rCurrentPage - 1, rPageSize));
        PaginationAssistant.assistant("message", model,
                messageService.findAllMessages(false, mCurrentPage - 1, mPageSize));

        model.addAttribute("title", "ModeratorPage");
        return "moderation/moderatorPage";
    }

    @GetMapping("/edit_message/{id}")
    public String editMessage(@PathVariable UUID id, Model model) {

        MessageDto message = messageService.getMessageById(id).get();
        model.addAttribute("message", message);

        List<RoomDto> rooms = roomService.findAllRooms();
        model.addAttribute("rooms", rooms);

        List<UserDto> users = userService.findAllUsersWithoutDisabled();
        model.addAttribute("users", users);

        model.addAttribute("title", "EditMessage");
        return "moderation/createOrEditMessage";
    }

    @GetMapping("/new_message")
    public String newMessage(Model model, Principal principal) {

        MessageDto message = new MessageDto();
        UserDto currentUser = userService.getUserByUserName(principal.getName()).get();
        message.setAuthor(currentUser);
        model.addAttribute("message", message);

        List<RoomDto> rooms = roomService.findAllRooms();
        model.addAttribute("rooms", rooms);

        List<UserDto> users = userService.findAllUsersWithoutDisabled();
        model.addAttribute("users", users);

        model.addAttribute("newMessage", true);
        model.addAttribute("title", "NewMessage");
        return "moderation/createOrEditMessage";
    }

    @PostMapping("/save_message")
    public String createMessage(@ModelAttribute("message") MessageDto message) {

        if (Objects.isNull(message.getId())) messageService.createMessage(message);
        else messageService.updateMessage(message);
        return "redirect:/moderator";
    }

    @GetMapping("/delete_message/{id}")
    public String deleteMessage(@PathVariable UUID id) {

        messageService.deleteMessage(id);
        return "redirect:/moderator";
    }

    @GetMapping("/edit_room/{id}")
    public String editRoom(@PathVariable UUID id, Model model) {

        RoomDto room = roomService.getRoomById(id);
        model.addAttribute("room", room);

        List<UserDto> users = userService.findAllUsersWithoutDisabled();
        model.addAttribute("users", users);

        model.addAttribute("title", "EditRoom");
        return "moderation/createOrEditRoom";
    }

    @GetMapping("/new_room")
    public String newRoom(Model model, Principal principal) {

        RoomDto room = new RoomDto();
        UserDto currentUser = userService.getUserByUserName(principal.getName()).get();
        room.setOwner(currentUser);
        model.addAttribute("room", room);

        List<UserDto> users = userService.findAllUsersWithoutDisabled();
        model.addAttribute("users", users);

        model.addAttribute("newRoom", true);
        model.addAttribute("title", "NewRoom");
        return "moderation/createOrEditRoom";
    }

    @PostMapping("/save_room")
    public String createRoom(@ModelAttribute("room") RoomDto room) {

        if (Objects.isNull(room.getId())) roomService.createRoom(room);
        else roomService.updateRoom(room);
        return "redirect:/moderator";
    }

    @GetMapping("/delete_room/{id}")
    public String deleteRoom(@PathVariable UUID id) {

        roomService.deleteRoom(id);
        return "redirect:/moderator";
    }

    @GetMapping("/new_room_user/{roomId}")
    public String newInterlocutor(@PathVariable UUID roomId, Model model) {

        InterlocutorDto newInterlocutor = new InterlocutorDto();
        newInterlocutor.setRoom(roomService.getRoomById(roomId));
        model.addAttribute("newInterlocutor", newInterlocutor);

        List<UserDto> users = userService.findAllUsersWithoutDisabled();
        model.addAttribute("users", users);

        model.addAttribute("returnTo", "save_interlocutor");
        model.addAttribute("title", "NewInterlocutor");
        return "moderation/addInterlocutorInRoom";
    }

    @PostMapping("/save_interlocutor")
    public String createInterlocutor(@ModelAttribute("newInterlocutor") InterlocutorDto newInterlocutor) {

        interlocutorService.createInterlocutor(newInterlocutor);
        return "redirect:/moderator";
    }

    @GetMapping("/delete_room_user/{roomId}/{userId}")
    public String deleteInterlocutor(@PathVariable UUID roomId, @PathVariable UUID userId) {

        interlocutorService.deleteInterlocutor(roomId, userId);
        return "redirect:/moderator";
    }

    @GetMapping("/delete_interlocutor/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_USER')")
    public String deleteInterlocutor(@PathVariable UUID id, HttpServletRequest request) {

        interlocutorService.deleteInterlocutor(id);
        return "redirect:".concat(request.getHeader("Referer"));
    }

    @GetMapping("/edit_list_room/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_USER')")
    public String editAndListRoom(@PathVariable UUID id, Model model,
                                  @RequestParam("page") Optional<Integer> page,
                                  @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        RoomDto room = roomService.getRoomById(id);
        model.addAttribute("room", room);

        List<UserDto> users = userService.findAllUsersWithoutDisabled();
        model.addAttribute("users", users);

        PaginationAssistant.assistant("interlocutor", model,
                interlocutorService.findAllInterlocutorsInRoomId(id, currentPage - 1, pageSize));

        model.addAttribute("title", "EditAndListRoom");
        return "moderation/editAndListRoom";
    }

    @PostMapping("/update_room")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_USER')")
    public String updateRoom2(@ModelAttribute("room") RoomDto room) {

        roomService.updateRoom(room);
        return "redirect:/";
    }

    @GetMapping("/new_interlocutor_room/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_USER')")
    public String newInterlocutorInRoom(@PathVariable UUID id, Model model) {

        InterlocutorDto newInterlocutor = new InterlocutorDto();
        newInterlocutor.setRoom(roomService.getRoomById(id));
        model.addAttribute("newInterlocutor", newInterlocutor);

        List<UserDto> users = userService.findAllUsersWithoutDisabled();
        model.addAttribute("users", users);

        model.addAttribute("returnTo", "add_interlocutor_room");
        model.addAttribute("title", "NewInterlocutorInRoom");
        return "moderation/addInterlocutorInRoom";
    }

    @PostMapping("/add_interlocutor_room")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_USER')")
    public String addInterlocutorInRoom(@ModelAttribute("interlocutor") InterlocutorDto interlocutor) {

        interlocutorService.createInterlocutor(interlocutor);
        return "redirect:/edit_list_room/".concat(interlocutor.getRoom().getId().toString());
    }
}
