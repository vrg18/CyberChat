package edu.vrg18.cyber_chat.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

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

@Entity
@Table(name = "users")
@Getter
@Setter
public class AppUser {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_name", length = 50, nullable = false, unique = true)
    private String userName;

    @Column(name = "encrypted_password", length = 128, nullable = false)
    private String encryptedPassword;

    private boolean enabled;

    private boolean bot;

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @OneToOne
    @JoinColumn(name = "room_id")
    @JsonBackReference
    private Room lastRoom;

    @Column(name = "last_activity", nullable = false)
    @DateTimeFormat(pattern ="dd.MM.yyyy HH:mm")
    private LocalDateTime lastActivity;

    @Transient
    private String newPassword;
}
