package edu.vrg18.cyber_chat.controller;

import edu.vrg18.cyber_chat.dto.RoomDto;
import edu.vrg18.cyber_chat.entity.Message;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.entity.User;
import edu.vrg18.cyber_chat.service.InterlocutorService;
import edu.vrg18.cyber_chat.service.MessageService;
import edu.vrg18.cyber_chat.service.RoomService;
import edu.vrg18.cyber_chat.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_USER')")
public class ChatController {

    private final UserService userService;
    private final MessageService messageService;
    private final RoomService roomService;
    private final InterlocutorService interlocutorService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatController(UserService userService, MessageService messageService, RoomService roomService,
                          InterlocutorService interlocutorService, SimpMessagingTemplate simpMessagingTemplate) {
        this.userService = userService;
        this.messageService = messageService;
        this.roomService = roomService;
        this.interlocutorService = interlocutorService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @GetMapping("/")
    public String chatPage(Model model, Principal principal) {

        User currentUser = userService.getUserByUserName(principal.getName()).get();
        String lastRoomId = currentUser.getLastRoom().getId().toString();
        return "redirect:/room/".concat(lastRoomId);
    }

    @GetMapping("/room/{id}")
    public String roomPage(@PathVariable UUID id, Model model, Principal principal,
                           @RequestParam("mPage") Optional<Integer> mPage,
                           @RequestParam("mSize") Optional<Integer> mSize,
                           @RequestParam("uPage") Optional<Integer> uPage,
                           @RequestParam("uSize") Optional<Integer> uSize) {

        int currentMPage = mPage.orElse(0);   // 0 - show from the last page
        int pageMSize = mSize.orElse(8);
        int currentUPage = uPage.orElse(1);
        int pageUSize = uSize.orElse(10);

        User currentUser = userService.getUserByUserName(principal.getName()).get();
        Room currentRoom = roomService.getRealRoomById(id);

        if (!currentUser.getLastRoom().getId().equals(id)) {
            currentUser.setLastRoom(currentRoom);
            userService.updateUser(currentUser);
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentUserId", currentUser.getId().toString());
        model.addAttribute("currentRoomId", id);

        List<RoomDto> rooms = roomService.findAllRoomsOfUserAndAllOpenRooms(currentUser);
        model.addAttribute("rooms", rooms);

        Page<Message> messagesPage = messageService.findAllMessagesByRoomAndMarkAsRead(currentRoom, currentUser,
                currentMPage - 1, pageMSize);
        model.addAttribute("messagesPage", messagesPage);
        int totalPages = messagesPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageMNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageMNumbers", pageMNumbers);
        }

        Message newMessage = new Message(null, null, currentUser, currentRoom, null);
        model.addAttribute("newMessage", newMessage);

        Page<User> usersPage = userService.findAllUsersWithoutDisabled(currentUPage - 1, pageUSize);
        model.addAttribute("usersPage", usersPage);
        totalPages = usersPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageUNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageUNumbers", pageUNumbers);
        }

        StringBuffer roomName = new StringBuffer(currentRoom.getName());
        roomName.append(" (");
        userService.findUsersInRoomId(id).forEach(u -> roomName.append(u.getFirstName()).append(", "));
        roomName.setLength(roomName.length() - 2);
        roomName.append(")");
        model.addAttribute("roomName", roomName);

        boolean isUserInRoom = interlocutorService.isUserInRoom(currentUser, currentRoom);
        model.addAttribute("isUserInRoom", isUserInRoom);

        model.addAttribute("title", "CyberChat");
//        model.addAttribute("message", "Добро пожаловать в CyberChat!");
        return "chatPage";
    }

    @GetMapping("/teteatete_room/{id}")
    public String newTeteATeteRoom(@PathVariable UUID id, Principal principal, HttpServletRequest request) {

        User currentUser = userService.getUserByUserName(principal.getName()).get();
        if (currentUser.getId().equals(id)) {
            String referer = request.getHeader("Referer");
            return "redirect:".concat(referer);
        } else {
            RoomDto teteATeteRoom = roomService.findOrCreateTeteATeteRoom(currentUser, userService.getUserById(id).get());
            return "redirect:/room/".concat(teteATeteRoom.getId().toString());
        }
    }

    @PostMapping("/send_message")
    public String sendMessage(@ModelAttribute("message") Message message, HttpServletRequest request) {

        messageService.createMessage(message);

        simpMessagingTemplate.convertAndSend("/topic/" + message.getRoom().getId().toString(),
                message.getAuthor().getId().toString());

        String referer = request.getHeader("Referer");
        return "redirect:".concat(referer);
    }

    @GetMapping("/lock_unlock_room/{id}")
    public String lockUnlockRoom(@PathVariable UUID id, HttpServletRequest request) {

        RoomDto room = roomService.getRoomById(id);
        room.setConfidential(!room.isConfidential());
        roomService.updateRoom(room);
        String referer = request.getHeader("Referer");
        return "redirect:".concat(referer);
    }
}
