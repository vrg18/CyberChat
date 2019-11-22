package edu.vrg18.cyber_chat.dto;

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
public class UserRoleDto {

    @Id
    private UUID id;
    private UserDto user;
    private RoleDto role;
}