package edu.vrg18.cyber_chat.repository;

import edu.vrg18.cyber_chat.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<AppUser, UUID> {

    AppUser findUserByUserName(String userName);
}
