package edu.vrg18.cyber_chat.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import edu.vrg18.cyber_chat.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private UUID id;
    private String userName;
    //    private String encryptedPassword;
    private boolean enabled;
    private boolean bot;
    private String firstName;
    private String lastName;
    private UUID lastRoomId;
    private LocalDateTime lastActivity;

    private String newPassword;
}
