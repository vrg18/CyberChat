package edu.vrg18.cyber_chat.controller;

import edu.vrg18.cyber_chat.dto.MessageDto;
import edu.vrg18.cyber_chat.dto.RoomDto;
import edu.vrg18.cyber_chat.dto.UserDto;
import edu.vrg18.cyber_chat.entity.Interlocutor;
import edu.vrg18.cyber_chat.service.InterlocutorService;
import edu.vrg18.cyber_chat.service.MessageService;
import edu.vrg18.cyber_chat.service.RoomService;
import edu.vrg18.cyber_chat.service.UserService;
import edu.vrg18.cyber_chat.utils.WebUtils;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        int rPageSize = rSize.orElse(10);
        int mCurrentPage = mPage.orElse(1);
        int mPageSize = mSize.orElse(8);

        org.springframework.security.core.userdetails.User loginedUser =
                (org.springframework.security.core.userdetails.User) ((Authentication) principal).getPrincipal();
        String userInfo = WebUtils.userToString(loginedUser);
        model.addAttribute("userInfo", userInfo);

        Page<RoomDto> roomsPage =
                roomService.findAllRooms(rCurrentPage - 1, rPageSize);
        model.addAttribute("rooms", roomsPage.getContent());
        int rTotalPages = roomsPage.getTotalPages();
        if (rTotalPages > 0) {
            List<Integer> rPageNumbers = IntStream.rangeClosed(1, rTotalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("rPageNumbers", rPageNumbers);
            model.addAttribute("rTotalPages", rTotalPages);
            model.addAttribute("rPageSize", rPageSize);
            model.addAttribute("rCurrentPage", roomsPage.getNumber() + 1);
        }

        Page<MessageDto> messagesPage =
                messageService.findAllMessages(false, mCurrentPage - 1, mPageSize);
        model.addAttribute("messages", messagesPage.getContent());
        int mTotalPages = messagesPage.getTotalPages();
        if (mTotalPages > 0) {
            List<Integer> mPageNumbers = IntStream.rangeClosed(1, mTotalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("mPageNumbers", mPageNumbers);
            model.addAttribute("mTotalPages", mTotalPages);
            model.addAttribute("mPageSize", mPageSize);
            model.addAttribute("mCurrentPage", messagesPage.getNumber() + 1);
        }

        List<Interlocutor> interlocutors = interlocutorService.findAllInterlocutors();
        model.addAttribute("interlocutors", interlocutors);

        model.addAttribute("title", "ModeratorPage");
        return "moderation/moderatorPage";
    }

    @GetMapping("/edit_message/{id}")
    public String editMessage(@PathVariable UUID id, Model model) {

        MessageDto message = messageService.getMessageById(id).get();
        model.addAttribute("message", message);

        List<RoomDto> rooms = roomService.findAllRooms(0, 100).getContent();
        model.addAttribute("rooms", rooms);

        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);

        model.addAttribute("title", "EditMessage");
        return "moderation/createOrEditMessage";
    }

    @PostMapping(value = "/save_message", params = "id!=")
    public String updateMessage(@ModelAttribute("message") MessageDto message) {

        messageService.updateMessage(message);
        return "redirect:/moderator";
    }

    @GetMapping("/new_message")
    public String newMessage(Model model, Principal principal) {

        List<RoomDto> rooms = roomService.findAllRooms(0, 100).getContent();
        model.addAttribute("rooms", rooms);

        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);

        UserDto currentUser = userService.getUserByUserName(principal.getName()).get();
        model.addAttribute("currentUserId", currentUser.getId());

        model.addAttribute("newMessage", true);
        model.addAttribute("title", "NewMessage");
        return "moderation/createOrEditMessage";
    }

    @PostMapping(value = "/save_message", params = "id=")
    public String createMessage(@ModelAttribute("message") MessageDto message) {

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

        RoomDto room = roomService.getRoomById(id);
        model.addAttribute("room", room);

        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);

        model.addAttribute("title", "EditRoom");
        return "moderation/createOrEditRoom";
    }

    @PostMapping(value = "/save_room", params = "id!=")
    public String updateRoom(@ModelAttribute("room") RoomDto room, BindingResult result) {

        roomService.updateRoom(room);
        return "redirect:/moderator";
    }

    @GetMapping("/new_room")
    public String newRoom(Model model, Principal principal) {

        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);

        UserDto currentUser = userService.getUserByUserName(principal.getName()).get();
        model.addAttribute("currentUserId", currentUser.getId());

        model.addAttribute("newRoom", true);
        model.addAttribute("title", "NewRoom");
        return "moderation/createOrEditRoom";
    }

    @PostMapping(value = "/save_room", params = "id=")
    public String createRoom(@ModelAttribute("room") RoomDto room) {

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

        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);

        List<RoomDto> rooms = roomService.findAllRooms(0, 100).getContent();
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

        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);

        List<RoomDto> rooms = roomService.findAllRooms(0, 100).getContent();
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

        RoomDto room = roomService.getRoomById(id);
        model.addAttribute("room", room);

        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);

        List<Interlocutor> interlocutors = interlocutorService.findAllInterlocutorsInRoomId(id);
        model.addAttribute("interlocutors", interlocutors);

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

        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);

        RoomDto room = roomService.getRoomById(id);
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
