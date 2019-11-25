package edu.vrg18.cyber_chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private UUID id;
    private String userName;
    private boolean enabled;
    private boolean bot;
    private String firstName;
    private String lastName;
    private UUID lastRoomId;
    private LocalDateTime lastActivity;

    private String newPassword;
    private List<RoleDto> roles;
}
