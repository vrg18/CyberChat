package edu.vrg18.cyber_chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomDto {

    private UUID id;
    private String name;
    private UserDto owner;
    private boolean confidential;
    private boolean closed;

    private int numberInterlocutors;
    private String unreadMessages;
}
