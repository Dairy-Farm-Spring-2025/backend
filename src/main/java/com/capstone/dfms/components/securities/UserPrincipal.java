package com.capstone.dfms.components.securities;

import com.capstone.dfms.models.UserEntity;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
@Getter
@Setter
@NoArgsConstructor
public class UserPrincipal implements UserDetails,OAuth2User{
    private UserEntity user;

    @Setter
    private Map<String, Object> attributes;

    public UserPrincipal(UserEntity user) {
        this.user = user;
    }

    private Collection<? extends GrantedAuthority> authorities;


    public UserPrincipal(UserEntity user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    public UserPrincipal(UserEntity user, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
        this.user = user;
        this.authorities = authorities != null ? authorities : Collections.emptyList();
        this.attributes = attributes != null ? attributes : Collections.emptyMap();
    }

    public static UserPrincipal create(UserEntity user) {
        List<GrantedAuthority> authorities = Collections.
                singletonList(new SimpleGrantedAuthority("ROLE_"+user.getRoleId().getName().toUpperCase()));

        return new UserPrincipal(
                user,
                authorities
        );
    }

    public static UserPrincipal create(UserEntity user, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }



    public Long getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }
    public String getFullName() {
        return user.getName();
    }


    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return user.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
