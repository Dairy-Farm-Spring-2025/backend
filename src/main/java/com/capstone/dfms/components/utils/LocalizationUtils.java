package com.capstone.dfms.components.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;



@RequiredArgsConstructor
@Component
public class LocalizationUtils {
    private static MessageSource messageSource;

    @Autowired
    public LocalizationUtils(MessageSource messageSource) {
        LocalizationUtils.messageSource = messageSource;
    }

    public static String getMessage(String key, Object... args) {
        if (messageSource == null) {
            throw new IllegalStateException("MessageSource has not been initialized.");
        }
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
}

