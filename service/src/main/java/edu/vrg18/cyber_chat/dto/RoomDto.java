package edu.vrg18.cyber_chat.dto;

import edu.vrg18.cyber_chat.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RoomDto {

    private UUID id;
    private String name;
    private User owner;
    private boolean confidential;
    private boolean closed;

    private int numberInterlocutors;
    private int unreadMessages;
}
