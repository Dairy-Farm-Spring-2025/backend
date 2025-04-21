package com.capstone.dfms.services;

import org.springframework.web.multipart.MultipartFile;

public interface IOcrServices {
    String readCccd(MultipartFile file) throws Exception;
}
