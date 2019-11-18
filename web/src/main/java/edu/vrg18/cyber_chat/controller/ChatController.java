package edu.vrg18.cyber_chat.controller;

import edu.vrg18.cyber_chat.dto.MessageDto;
import edu.vrg18.cyber_chat.dto.RoomDto;
import edu.vrg18.cyber_chat.dto.UserDto;
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
    public String chatPage(Principal principal) {

        return "redirect:/room/".concat(userService.getLastUserRoomId(principal.getName()));
   }

    @GetMapping("/room/{id}")
    public String roomPage(@PathVariable UUID id, Model model, Principal principal,
                           @RequestParam("mPage") Optional<Integer> mPage,
                           @RequestParam("mSize") Optional<Integer> mSize,
                           @RequestParam("uPage") Optional<Integer> uPage,
                           @RequestParam("uSize") Optional<Integer> uSize) {

        int mCurrentPage = mPage.orElse(0);   // 0 - show from the last page
        int mPageSize = mSize.orElse(8);
        int uCurrentPage = uPage.orElse(1);
        int uPageSize = uSize.orElse(10);

        UserDto currentUser = userService.getUserByUserName(principal.getName()).get();
        RoomDto currentRoom = roomService.getRoomById(id);

        if (!currentUser.getLastRoomId().equals(id)) {
            currentUser.setLastRoomId(id);
            userService.updateUser(currentUser);
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentUserId", currentUser.getId().toString());
        model.addAttribute("currentRoomId", id);

        List<RoomDto> rooms = roomService.findAllRoomsOfUserAndAllPublicRooms(currentUser);
        model.addAttribute("rooms", rooms);

        Page<MessageDto> messagesPage = messageService.findAllMessagesByRoomAndMarkAsRead(currentRoom, currentUser,
                mCurrentPage - 1, mPageSize);
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

        model.addAttribute("newMessage", messageService.newMessage(currentUser, currentRoom));

        Page<UserDto> usersPage = userService.findAllUsersWithoutDisabled(uCurrentPage - 1, uPageSize);
        model.addAttribute("users", usersPage.getContent());
        int uTotalPages = usersPage.getTotalPages();
        if (uTotalPages > 0) {
            List<Integer> uPageNumbers = IntStream.rangeClosed(1, uTotalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("uPageNumbers", uPageNumbers);
            model.addAttribute("uTotalPages", uTotalPages);
            model.addAttribute("uPageSize", uPageSize);
            model.addAttribute("uCurrentPage", usersPage.getNumber() + 1);
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

        UserDto currentUser = userService.getUserByUserName(principal.getName()).get();
        if (currentUser.getId().equals(id)) {
            String referer = request.getHeader("Referer");
            return "redirect:".concat(referer);
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
