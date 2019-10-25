package edu.vrg18.cyber_chat.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
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

    @Column(name = "last_activity", nullable = false)
    @DateTimeFormat(pattern ="dd.MM.yyyy HH:mm")
    private Date lastActivity;

    @Transient
    private String newPassword;

    private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public void setEncryptedPassword(String password) {
        this.encryptedPassword = encoder.encode(password);
    }
}
