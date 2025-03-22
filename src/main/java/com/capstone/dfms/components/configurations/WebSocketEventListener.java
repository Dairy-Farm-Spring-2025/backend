package com.capstone.dfms.components.configurations;

import com.capstone.dfms.components.securities.UserPrincipal;
import com.capstone.dfms.components.utils.JwtUtil;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import java.util.Map;

@Component
public class WebSocketEventListener {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IUserRepository userRepository;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();

        if (sessionAttributes != null && sessionAttributes.containsKey("token")) {
            String token = (String) sessionAttributes.get("token");
            String userId = jwtUtil.extractUserId(token);

            if (userId != null) {
                sessionAttributes.put("userId", userId);
                System.out.println("📢 WebSocket kết nối - UserID: " + userId);

                UserEntity user = userRepository.findById(Long.parseLong(userId)).orElse(null);

                if (user != null) {
                    UserPrincipal userPrincipal = new UserPrincipal(user);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("✅ Đã xác thực người dùng thành công!");
                } else {
                    System.out.println("⚠ Không tìm thấy UserEntity trong database!");
                }
            } else {
                System.out.println("⚠ Token không hợp lệ!");
            }
        } else {
            System.out.println("⚠ Không tìm thấy token trong session!");
        }
    }
}
