package com.capstone.dfms.components.securities;

import com.capstone.dfms.components.exceptions.DataNotFoundException;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailService implements UserDetailsService {
    @Autowired
    IUserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userName)
            throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(userName)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with user name : " + userName)
                );

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        UserEntity user = userRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("User", "id", id)
        );

        return UserPrincipal.create(user);
    }
}
