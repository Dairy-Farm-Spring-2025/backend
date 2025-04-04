package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.HealthRecordEntity;
import com.capstone.dfms.requests.CowCreateRequest;
import com.capstone.dfms.requests.CowUpdateRequest;
import com.capstone.dfms.responses.BulkCowHealthRecordResponse;
import com.capstone.dfms.responses.CowPenBulkResponse;
import com.capstone.dfms.responses.CowResponse;
import com.capstone.dfms.services.ICowServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import static com.capstone.dfms.mappers.ICowMapper.INSTANCE;

@RestController
@RequestMapping("${app.api.version.v1}/cows")
@RequiredArgsConstructor
public class CowController {
    private final ICowServices cowServices;
    @Autowired
    private ResourceLoader resourceLoader;

    @PostMapping("/create")
    public CoreApiResponse<CowResponse> createCow(@Valid @RequestBody CowCreateRequest cowCreateRequest) {
        CowResponse cowResponse = cowServices.createCow(INSTANCE.toModel(cowCreateRequest));
        return CoreApiResponse.success(cowResponse);
    }

    @PostMapping("/create-bulk")
    public ResponseEntity<?> createBulkCow(@Valid @RequestBody List<CowCreateRequest> requests) {
        return CoreApiResponse.success(cowServices.createBulkCow(requests));
    }

    @PutMapping("/{id}")
    public CoreApiResponse<CowResponse> updateCow(@PathVariable Long id, @Valid @RequestBody CowUpdateRequest cowUpdateRequest) {
        CowResponse cowResponse = cowServices.updateCow(id, cowUpdateRequest);
        return CoreApiResponse.success(cowResponse);
    }

    @GetMapping("/{id}")
    public CoreApiResponse<CowResponse> getCowById(@PathVariable Long id) {
        CowResponse cowResponse = cowServices.getCowById(id);
        return CoreApiResponse.success(cowResponse);
    }

    @GetMapping
    public CoreApiResponse<List<CowResponse>> getAllCows() {
        List<CowResponse> cowResponses = cowServices.getAllCows();
        return CoreApiResponse.success(cowResponses);
    }

    @GetMapping("/qr/{id}")
    public ResponseEntity<byte[]> generateCowQRCode(@PathVariable Long id) {
            byte[] qrCodeImage = cowServices.generateCowQRCode(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrCodeImage);
    }

    @PostMapping("/cow-from-excel")
    public ResponseEntity<?> getCowsFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            BulkCowHealthRecordResponse result = cowServices.getInformationFromExcel(file);

            if (!result.getCowResponseCowPenBulkResponse().getErrors().isEmpty() ||
                    !result.getHealthRecordEntityCowPenBulkResponse().getErrors().isEmpty()) {
                return ResponseEntity.status(400).body(result);
            }

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return (ResponseEntity<CowPenBulkResponse<CowResponse>>) ResponseEntity.badRequest();
        }
    }


    @GetMapping("/templates/download/cow-bulk-excel")
    public ResponseEntity<Void> downloadExcelTemplate(HttpServletRequest request) {
        String fileUrl = "/document/Template%20Cow%20Import.xlsx";
        return ResponseEntity.status(HttpStatus.FOUND) // 302 Redirect
                .location(URI.create(fileUrl))
                .build();
    }


}
