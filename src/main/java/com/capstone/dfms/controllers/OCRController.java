package com.capstone.dfms.controllers;

import com.capstone.dfms.services.IOcrServices;
import com.capstone.dfms.services.impliments.OcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${app.api.version.v1}/ocr")
@RequiredArgsConstructor
public class OCRController {
    @Autowired
    private IOcrServices ocrService;

    @PostMapping("/cccd")
    public ResponseEntity<?> uploadCccd(@RequestParam("file") MultipartFile file) {
        try {
            String result = ocrService.readCccd(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
}
