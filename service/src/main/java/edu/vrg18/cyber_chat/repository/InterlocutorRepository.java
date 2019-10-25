package edu.vrg18.cyber_chat.repository;

import edu.vrg18.cyber_chat.entity.Interlocutor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InterlocutorRepository extends JpaRepository<Interlocutor, UUID> {
}
