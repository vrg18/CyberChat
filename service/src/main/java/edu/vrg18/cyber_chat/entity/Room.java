package edu.vrg18.cyber_chat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    @OneToOne
    @JoinColumn(name = "user_id")
    private AppUser owner;

    private boolean confidential;

    private boolean closed;
}