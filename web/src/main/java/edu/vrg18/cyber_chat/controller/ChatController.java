package edu.vrg18.cyber_chat.controller;

import edu.vrg18.cyber_chat.dto.MessageDto;
import edu.vrg18.cyber_chat.dto.RoomDto;
import edu.vrg18.cyber_chat.dto.UserDto;
import edu.vrg18.cyber_chat.service.InterlocutorService;
import edu.vrg18.cyber_chat.service.MessageService;
import edu.vrg18.cyber_chat.service.RoomService;
import edu.vrg18.cyber_chat.service.UserService;
import edu.vrg18.cyber_chat.util.PaginationAssistant;
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
import java.util.Optional;
import java.util.UUID;

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
    public String chatPage(Principal principal) {

        return "redirect:/room/".concat(userService.getLastUserRoomId(principal.getName()));
   }

    @GetMapping("/room/{id}")
    public String roomPage(@PathVariable UUID id, Model model, Principal principal,
                           @RequestParam("rPage") Optional<Integer> rPage,
                           @RequestParam("rSize") Optional<Integer> rSize,
                           @RequestParam("mPage") Optional<Integer> mPage,
                           @RequestParam("mSize") Optional<Integer> mSize,
                           @RequestParam("uPage") Optional<Integer> uPage,
                           @RequestParam("uSize") Optional<Integer> uSize) {

        int rCurrentPage = rPage.orElse(1);
        int rPageSize = rSize.orElse(8);
        int mCurrentPage = mPage.orElse(0);   // 0 - show from the last page
        int mPageSize = mSize.orElse(8);
        int uCurrentPage = uPage.orElse(1);
        int uPageSize = uSize.orElse(10);

        UserDto currentUser = userService.getUserByUserName(principal.getName()).get();
        RoomDto currentRoom = roomService.getRoomById(id);

        userService.setLastUserRoom(currentUser, id);
        model.addAttribute("currentUserId", currentUser.getId());
        model.addAttribute("currentRoomId", id);

        PaginationAssistant.assistant("room", model,
                roomService.findAllRoomsOfUserAndAllPublicRooms(currentUser, rCurrentPage - 1, rPageSize));
        PaginationAssistant.assistant("message", model,
                messageService.findAllMessagesByRoomAndMarkAsRead(currentRoom, currentUser,
                        mCurrentPage - 1, mPageSize));
        PaginationAssistant.assistant("user", model,
                userService.findAllUsersWithoutDisabled(uCurrentPage - 1, uPageSize));

        model.addAttribute("newMessage", messageService.newMessage(currentUser, currentRoom));
        model.addAttribute("roomName", roomService.getFullNameOfRoom(currentRoom));
        model.addAttribute("isUserInRoom", interlocutorService.isUserInRoom(currentUser, currentRoom));

        model.addAttribute("title", "CyberChat");
        return "chatPage";
    }

    @GetMapping("/teteatete_room/{id}")
    public String newTeteATeteRoom(@PathVariable UUID id, Principal principal, HttpServletRequest request) {

        UserDto currentUser = userService.getUserByUserName(principal.getName()).get();
        if (currentUser.getId().equals(id)) {
            return "redirect:".concat(request.getHeader("Referer"));
        } else {
            RoomDto teteATeteRoom = roomService.findOrCreateTeteATeteRoom(currentUser, userService.getUserById(id).get());
            return "redirect:/room/".concat(teteATeteRoom.getId().toString());
        }
    }

    @PostMapping("/send_message")
    public String sendMessage(@ModelAttribute("message") MessageDto message, HttpServletRequest request) {

        messageService.createMessage(message);

        simpMessagingTemplate.convertAndSend("/topic/" + message.getRoom().getId().toString(),
                message.getAuthor().getId().toString());

        return "redirect:".concat(request.getHeader("Referer"));
    }

    @GetMapping("/lock_unlock_room/{id}")
    public String lockUnlockRoom(@PathVariable UUID id, HttpServletRequest request) {

        RoomDto room = roomService.getRoomById(id);
        room.setConfidential(!room.isConfidential());
        roomService.updateRoom(room);
        return "redirect:".concat(request.getHeader("Referer"));
    }
}
