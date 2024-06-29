package com.adalytics.adalytics_backend.models.entities;

import com.adalytics.adalytics_backend.enums.Role;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Document("users")
public class User implements UserDetails {
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    /**
     * {@link Role}
     */
    private String role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }
}
