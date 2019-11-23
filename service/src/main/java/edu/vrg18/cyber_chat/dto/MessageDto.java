package edu.vrg18.cyber_chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

//@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {

//    @Id
    private UUID id;
    private LocalDateTime date;
    private UserDto author;
    private RoomDto room;
    private String text;
}
