package com.capstone.dfms.services.impliments;

import com.capstone.dfms.services.IOcrServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class OcrService implements IOcrServices {

    private final RestTemplate restTemplate;

    @Value("${app.fpt.api_key}")
    private String apiKey;

    @Value("${app.fpt.ocr_url}")
    private String ocrUrl;

    public OcrService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String readCccd(MultipartFile file) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("api-key", apiKey);

        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", resource);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                ocrUrl,
                HttpMethod.POST,
                request,
                String.class
        );

        return response.getBody();
    }
}