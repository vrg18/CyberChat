package edu.vrg18.cyber_chat.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Data
public class Message {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "interlocutor_id", nullable = false)
    private Interlocutor interlocutor;

    @Column(length = 255, nullable = false)
    private String text;

}
