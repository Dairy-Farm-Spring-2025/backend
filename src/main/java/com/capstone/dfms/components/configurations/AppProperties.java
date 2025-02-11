package com.capstone.dfms.components.configurations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private final Auth auth = new Auth();

    @Getter
    @Setter
    public static class Auth {

        private String tokenSecret;


        private int refreshTokenExpirationMsec;


        private int accessTokenExpirationMsec;
    }

}

