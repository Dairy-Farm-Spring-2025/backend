package com.capstone.dfms.components.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Hỗ trợ Java 8 time
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); // Tắt timestamp
        mapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd")); // Định dạng ngày
        mapper.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Ho_Chi_Minh")); // Múi giờ
        return mapper;
    }
}
