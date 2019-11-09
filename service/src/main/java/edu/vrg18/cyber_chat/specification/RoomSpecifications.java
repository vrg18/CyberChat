package edu.vrg18.cyber_chat.specification;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.entity.Interlocutor;
import edu.vrg18.cyber_chat.entity.Room;
import org.springframework.data.jpa.domain.Specification;

public class RoomSpecifications {

    public static Specification<Room> openRoom() {
        return (root, query, cb) -> cb.equal(root.get(Room_.closed), false);
    }

    public static Specification<Room> publicRoom() {
        return (root, query, cb) -> cb.equal(root.get(Room_.confidential), false);
    }

    public static Specification<Room> userRoom(AppUser user) {
        return (root, query, cb) -> cb.equal(root.get(Interlocutor_.user), user).room;
    }
}