package edu.vrg18.cyber_chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {

    private UUID id;
    private LocalDateTime date;
    private UserDto author;
    private RoomDto room;
    private String text;
}
