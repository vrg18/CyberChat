package edu.vrg18.cyber_chat.repository;

import edu.vrg18.cyber_chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
}
