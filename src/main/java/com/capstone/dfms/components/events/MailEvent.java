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



    public MailEvent(Object source, UserEntity user, String url, String type) {
        super(source);
        this.user = user;
        Url = url;
        this.type = type;
    }


}

