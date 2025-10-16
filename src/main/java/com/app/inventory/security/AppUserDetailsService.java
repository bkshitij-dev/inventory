package com.app.inventory.security;

import com.app.inventory.constant.MessageConstants;
import com.app.inventory.model.User;
import com.app.inventory.service.AuthService;
import com.app.inventory.service.CustomMessageSource;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final AuthService authenticationService;
    private final CustomMessageSource customMessageSource;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = authenticationService.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException(
                        customMessageSource.getMessage(MessageConstants.AUTH_USER_NOT_EXISTS,
                                new Object[]{usernameOrEmail})));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();
    }
}
