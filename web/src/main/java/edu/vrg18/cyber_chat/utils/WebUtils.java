package edu.vrg18.cyber_chat.utils;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class WebUtils {

    public static String userToString(org.springframework.security.core.userdetails.User user) {
        StringBuilder sb = new StringBuilder();

        sb.append("Current user: ").append(user.getUsername());

        Collection<GrantedAuthority> authorities = user.getAuthorities();
        if (authorities != null && !authorities.isEmpty()) {
            sb.append("<br>Available roles: ");
            boolean first = true;
            for (GrantedAuthority a : authorities) {
                if (first) {
                    sb.append(a.getAuthority());
                    first = false;
                } else {
                    sb.append(", ").append(a.getAuthority());
                }
            }
        }
        return sb.toString();
    }
}
