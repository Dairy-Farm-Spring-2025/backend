package com.capstone.dfms.components.configurations;

import com.capstone.dfms.components.utils.JwtUtil;
import com.capstone.dfms.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
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

        // Kiểm tra null trước khi truy xuất sessionAttributes
        if (headerAccessor.getSessionAttributes() == null) {
            System.out.println("⚠ SessionAttributes chưa được khởi tạo!");
            return;
        }

        // Lấy token từ sessionAttributes
        String token = (String) headerAccessor.getSessionAttributes().get("token");

        if (token == null || token.isEmpty()) {
            System.out.println("⚠ Không tìm thấy token trong session!");
            return;
        }

        System.out.println("🔍 Token nhận được trong WebSocketEventListener: " + token);

        // Xác thực token
        String userId = jwtUtil.extractUserId(token);
        if (userId != null) {
            headerAccessor.getSessionAttributes().put("userId", userId);
            System.out.println("📢 WebSocket kết nối - UserID: " + userId);
        } else {
            System.out.println("⚠ Token không hợp lệ!");
        }
    }


}
