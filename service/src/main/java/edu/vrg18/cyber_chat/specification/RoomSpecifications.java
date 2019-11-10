package edu.vrg18.cyber_chat.specification;

import edu.vrg18.cyber_chat.entity.Interlocutor;
import edu.vrg18.cyber_chat.entity.Interlocutor_;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.entity.Room_;
import edu.vrg18.cyber_chat.entity.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

public class RoomSpecifications {

    public static Specification<Room> openRoom() {
        return (root, query, cb) -> cb.equal(root.get(Room_.closed), false);
    }

    public static Specification<Room> publicRoom() {
        return (root, query, cb) -> cb.equal(root.get(Room_.confidential), false);
    }

    public static Specification<Room> userRoom(User user) {
        return (roomRoot, query, cb) -> {
            Root<Interlocutor> interlocutorRoot = query.from(Interlocutor.class);
            Predicate predicate = cb.equal(interlocutorRoot.get(Interlocutor_.USER), user);
            query.where(predicate);
            query.select(interlocutorRoot.get(Interlocutor_.ROOM));
            query.distinct(true);
            return query.getRestriction();
        };
    }
}