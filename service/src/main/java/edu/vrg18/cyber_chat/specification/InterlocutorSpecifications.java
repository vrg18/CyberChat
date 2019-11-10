package edu.vrg18.cyber_chat.specification;

import edu.vrg18.cyber_chat.entity.Interlocutor;
import edu.vrg18.cyber_chat.entity.Interlocutor_;
import edu.vrg18.cyber_chat.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class InterlocutorSpecifications {

    public static Specification<Interlocutor> userInterlocutor(User user) {
        return (root, query, cb) -> cb.equal(root.get(Interlocutor_.USER), user);
    }
}