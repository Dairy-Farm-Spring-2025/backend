package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.requests.*;
import com.capstone.dfms.responses.CowPenBulkResponse;
import com.capstone.dfms.responses.CowPenResponse;
import com.capstone.dfms.services.ICowPenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.capstone.dfms.mappers.ICowPenMapper.INSTANCE;

@RestController
@RequestMapping("${app.api.version.v1}/cow-pens")
@RequiredArgsConstructor
public class CowPenController {
    private final ICowPenService cowPenService;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @PostMapping
    public CoreApiResponse<CowPenResponse> create(@Valid @RequestBody CowPenCreateRequest request) {
        CowPenResponse response = cowPenService.create(INSTANCE.toModel(request));
        return CoreApiResponse.success(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @PostMapping("/create")
    public CoreApiResponse<CowPenResponse> createCowPen(@Valid @RequestBody CowPenCreateRequest request) {
        CowPenResponse response = cowPenService.createCowPen(INSTANCE.toModel(request));
        return CoreApiResponse.success(response,response.getMessage());
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @GetMapping
    public CoreApiResponse<List<CowPenResponse>> getAll() {
        List<CowPenResponse> responseList = cowPenService.getAll();
        return CoreApiResponse.success(responseList);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @GetMapping("/{penId}/{cowId}/{fromDate}")
    public CoreApiResponse<CowPenResponse> getById(
            @PathVariable Long penId,
            @PathVariable Long cowId,
            @PathVariable String fromDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse(fromDate, formatter);
        CowPenResponse response = cowPenService.getById(penId, cowId, date);
        return CoreApiResponse.success(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @PutMapping("/{penId}/{cowId}/{fromDate}")
    public CoreApiResponse<CowPenResponse> update(
            @PathVariable Long penId,
            @PathVariable Long cowId,
            @PathVariable LocalDateTime fromDate,
            @RequestBody CowPenUpdateRequest request) {
        CowPenResponse response = cowPenService.update(penId, cowId, fromDate, INSTANCE.toModel(request));
        return CoreApiResponse.success(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @DeleteMapping("/{penId}/{cowId}/{fromDate}")
    public CoreApiResponse<Void> delete(
            @PathVariable Long penId,
            @PathVariable Long cowId,
            @PathVariable LocalDateTime fromDate) {
        cowPenService.delete(penId, cowId, fromDate);
        return CoreApiResponse.success("Delete successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @GetMapping("/pen/{penId}")
    public CoreApiResponse<List<CowPenResponse>> getByPenId(
            @PathVariable Long penId) {
        List<CowPenResponse> response = cowPenService.getCowPenFollowPenId(penId);
        return CoreApiResponse.success(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @GetMapping("/cow/{cowId}")
    public CoreApiResponse<List<CowPenResponse>> getByCowId(
            @PathVariable Long cowId) {
        List<CowPenResponse> response = cowPenService.getCowPenFollowCowId(cowId);
        return CoreApiResponse.success(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @PostMapping("/create-bulk")
    public CoreApiResponse<CowPenBulkResponse> createBulk(@Valid @RequestBody CowPenBulkRequest cowPenBulkRequest){
        CowPenBulkResponse<CowPenResponse> cowPenBulkResponse =
                cowPenService.createBulkCowPen(cowPenBulkRequest);
        return CoreApiResponse.success(cowPenBulkResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @PostMapping("/moving-pen")
    public CoreApiResponse<CowPenResponse> movingPen(@Valid @RequestBody CowPenMovingRequest request){
        CowPenResponse cowPenResponse = cowPenService.movingPen(request);
        return CoreApiResponse.success(cowPenResponse);
    }
}
