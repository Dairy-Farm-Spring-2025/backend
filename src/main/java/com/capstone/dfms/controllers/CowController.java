package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.requests.CowCreateRequest;
import com.capstone.dfms.requests.CowUpdateRequest;
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

    @PostMapping("/import")
    public ResponseEntity<?> importCows(@RequestParam("file") MultipartFile file) {
        try {
            CowPenBulkResponse<CowResponse> result = cowServices.saveCowsFromExcel(file);

            if (!result.getErrors().isEmpty()) {
                return ResponseEntity.status(400).body(result);
            }

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return (ResponseEntity<CowPenBulkResponse<CowResponse>>) ResponseEntity.badRequest();
        }
    }

    @GetMapping("/cow-from-excel")
    public ResponseEntity<?> getCowsFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            CowPenBulkResponse<CowResponse> result = cowServices.getCowsFromExcel(file);

            if (!result.getErrors().isEmpty()) {
                return ResponseEntity.status(400).body(result);
            }

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return (ResponseEntity<CowPenBulkResponse<CowResponse>>) ResponseEntity.badRequest();
        }
    }

//    @GetMapping("/api/templates/download/excel")
//    public ResponseEntity<Resource> getExcelTemplate() throws IOException {
//        Resource resource = new ClassPathResource("static/document/Template Cow Import.xlsx");
//
//        if (!resource.exists()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Template Cow Import.xlsx")
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
//    }


    @GetMapping("/templates/download/cow-bulk-excel")
    public ResponseEntity<Void> downloadExcelTemplate(HttpServletRequest request) {
        String fileUrl = "/document/Template%20Cow%20Import.xlsx";
        return ResponseEntity.status(HttpStatus.FOUND) // 302 Redirect
                .location(URI.create(fileUrl))
                .build();
    }


}
