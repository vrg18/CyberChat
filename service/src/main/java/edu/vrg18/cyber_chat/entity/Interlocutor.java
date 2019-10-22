package edu.vrg18.cyber_chat.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "interlocutors")
@Data
public class Interlocutor {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 50, nullable = false, unique = true)
    private String name;

    @Column(name = "encrypted_password", length = 128, nullable = false)
    private String encryptedPassword;

    private boolean enabled;

    private boolean bot;

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "last_activity", nullable = false)
    private Date lastActivity;

}
