package com.capstone.dfms.components.events;

import com.capstone.dfms.models.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class MailEvent extends ApplicationEvent {

    private UserEntity user;
    private String Url;
    private String type;
    private String password;



    public MailEvent(String password, Object source, UserEntity user, String type) {
        super(source);
        this.user = user;
        this.type = type;
        this.password = password;
    }

    public MailEvent(Object source, UserEntity user, String url, String type) {
        super(source);
        this.user = user;
        Url = url;
        this.type = type;
    }

    public MailEvent(Object source, UserEntity user, String type) {
        super(source);
        this.user = user;
        this.type = type;
    }


}

