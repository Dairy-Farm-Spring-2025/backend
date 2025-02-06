package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.requests.*;
import com.capstone.dfms.responses.CowPenBulkResponse;
import com.capstone.dfms.responses.CowPenResponse;
import com.capstone.dfms.services.ICowPenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.capstone.dfms.mappers.ICowPenMapper.INSTANCE;

@RestController
@RequestMapping("${app.api.version.v1}/cow-pens")
@RequiredArgsConstructor
public class CowPenController {
    private final ICowPenService cowPenService;

    @PostMapping
    public CoreApiResponse<CowPenResponse> create(@Valid @RequestBody CowPenCreateRequest request) {
        CowPenResponse response = cowPenService.create(INSTANCE.toModel(request));
        return CoreApiResponse.success(response);
    }

    @GetMapping
    public CoreApiResponse<List<CowPenResponse>> getAll() {
        List<CowPenResponse> responseList = cowPenService.getAll();
        return CoreApiResponse.success(responseList);
    }

    // Read by ID
    @GetMapping("/{penId}/{cowId}/{fromDate}")
    public CoreApiResponse<CowPenResponse> getById(
            @PathVariable Long penId,
            @PathVariable Long cowId,
            @PathVariable String fromDate) {
        LocalDate date = LocalDate.parse(fromDate); // Convert String to LocalDate
        CowPenResponse response = cowPenService.getById(penId, cowId, date);
        return CoreApiResponse.success(response);
    }

    // Update
    @PutMapping("/{penId}/{cowId}/{fromDate}")
    public CoreApiResponse<CowPenResponse> update(
            @PathVariable Long penId,
            @PathVariable Long cowId,
            @PathVariable String fromDate,
            @RequestBody CowPenUpdateRequest request) {
        LocalDate date = LocalDate.parse(fromDate); // Convert String to LocalDate
        CowPenResponse response = cowPenService.update(penId, cowId, date, INSTANCE.toModel(request));
        return CoreApiResponse.success(response);
    }

    // Delete
    @DeleteMapping("/{penId}/{cowId}/{fromDate}")
    public CoreApiResponse<Void> delete(
            @PathVariable Long penId,
            @PathVariable Long cowId,
            @PathVariable String fromDate) {
        LocalDate date = LocalDate.parse(fromDate); // Convert String to LocalDate
        cowPenService.delete(penId, cowId, date);
        return CoreApiResponse.success("Delete successfully");
    }

    @GetMapping("/pen/{penId}")
    public CoreApiResponse<List<CowPenResponse>> getByPenId(
            @PathVariable Long penId) {
        List<CowPenResponse> response = cowPenService.getCowPenFollowPenId(penId);
        return CoreApiResponse.success(response);
    }

    @GetMapping("/cow/{cowId}")
    public CoreApiResponse<List<CowPenResponse>> getByCowId(
            @PathVariable Long cowId) {
        List<CowPenResponse> response = cowPenService.getCowPenFollowCowId(cowId);
        return CoreApiResponse.success(response);
    }

    //===========================================================================================
//    @GetMapping("/approve")
//    public CoreApiResponse<CowPenResponse> approve(
//            @Valid @RequestBody CowPenApproveRequest cowPenApproveRequest) {
////        LocalDate date = LocalDate.parse();
//        cowPenService.approveOrRejectMovePen(cowPenApproveRequest.getPenId(),
//                cowPenApproveRequest.getCowId(),
//                cowPenApproveRequest.getFromDate(),
//                cowPenApproveRequest.isApproval());
//        return CoreApiResponse.success("");
//    }

    @GetMapping("/approve/{penId}/{cowId}/{fromDate}/{approval}")
    public CoreApiResponse<CowPenResponse> approve(
            @PathVariable Long penId,
            @PathVariable Long cowId,
            @PathVariable String fromDate,
            @PathVariable boolean approval) {
        LocalDate date = LocalDate.parse(fromDate); // Convert String to LocalDate
        CowPenResponse cowPenResponse = cowPenService.approveOrRejectMovePen(penId, cowId, date, approval);
        return CoreApiResponse.success(cowPenResponse);
    }

    @PostMapping("/create-bulk")
    public CoreApiResponse<CowPenBulkResponse> createBulk(@Valid @RequestBody CowPenBulkRequest cowPenBulkRequest){
        CowPenBulkResponse<CowPenResponse> cowPenBulkResponse =
                cowPenService.createBulkCowPen(cowPenBulkRequest);
        return CoreApiResponse.success(cowPenBulkResponse);
    }

    @PostMapping("/moving-pen")
    public CoreApiResponse<CowPenResponse> movingPen(@Valid @RequestBody CowPenMovingRequest request){
        CowPenResponse cowPenResponse = cowPenService.movingPen(request);
        return CoreApiResponse.success(cowPenResponse);
    }
}
