package com.capstone.dfms.components.configurations;

import com.capstone.dfms.components.securities.UserPrincipal;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthenticationInterceptor implements ChannelInterceptor {

    @Autowired
    private IUserService userService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand() == StompCommand.CONNECT) {
            // Get the userId from the WebSocket headers (e.g., if you're using a token or userId)
            String userId = accessor.getFirstNativeHeader("userId");

            // Assuming you have a service that can fetch the user by their ID
            UserEntity userEntity = userService.getUserById(Long.parseLong(userId));

            // Create a UserPrincipal and authenticate
            UserPrincipal userPrincipal = UserPrincipal.create(userEntity);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            accessor.setUser(authentication);
        }

        return message;
    }
}

