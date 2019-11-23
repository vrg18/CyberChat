package edu.vrg18.cyber_chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.Id;
import java.util.UUID;

//@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomDto {

//    @Id
    private UUID id;
    private String name;
//    @JsonProperty("userId")
    private UserDto owner;
    private boolean confidential;
    private boolean closed;

    private int numberInterlocutors;
    private String unreadMessages;
}
