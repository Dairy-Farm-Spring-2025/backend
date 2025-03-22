package com.capstone.dfms.components.configurations;

import com.capstone.dfms.components.securities.TokenProvider;
import com.capstone.dfms.components.securities.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Autowired
    private TokenProvider tokenProvider; // Class xử lý JWT

    @Autowired
    private UserDetailService userDetailsService; // Service để lấy user từ DB

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> tokenList = accessor.getNativeHeader("Authorization");

            if (tokenList == null || tokenList.isEmpty()) {
                throw new IllegalArgumentException("Thiếu token trong kết nối WebSocket!");
            }

            // Lấy token từ header
            String token = tokenList.get(0).replace("Bearer ", "");

            // Kiểm tra token hợp lệ
            if (!tokenProvider.validateToken(token)) {
                throw new IllegalArgumentException("Token không hợp lệ!");
            }

            // Giải mã token để lấy userId
            Long userId = tokenProvider.getUserIdFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserById(userId);

            // Tạo đối tượng Authentication
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // Lưu Authentication vào SecurityContext để sử dụng sau này
            SecurityContextHolder.getContext().setAuthentication(authentication);
            accessor.setUser(authentication);
        }

        return message;
    }
}
