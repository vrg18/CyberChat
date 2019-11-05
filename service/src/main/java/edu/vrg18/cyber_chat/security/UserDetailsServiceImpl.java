package edu.vrg18.cyber_chat.security;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.entity.UserRole;
import edu.vrg18.cyber_chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;
    private EntityManager entityManager;

    @Autowired
    private void setRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        AppUser user = userRepository.findAppUserByUserName(userName).orElseThrow(()
                -> new UsernameNotFoundException("User " + userName + " was not found in the database"));

        // [ROLE_USER, ROLE_ADMIN,..]
        List<String> roleNames = getRoleNames(user.getId());

        List<GrantedAuthority> grantList = new ArrayList<>();
        if (roleNames != null) {
            for (String role : roleNames) {
                // ROLE_USER, ROLE_ADMIN,..
                GrantedAuthority authority = new SimpleGrantedAuthority(role);
                grantList.add(authority);
            }
        }

        return new User(user.getUserName(), user.getEncryptedPassword(), grantList);
    }

    private List<String> getRoleNames(UUID userId) {

        String sql = "Select ur.role.name from " + UserRole.class.getName() + " ur where ur.user.id = :userId ";

        Query query = this.entityManager.createQuery(sql, String.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
}
