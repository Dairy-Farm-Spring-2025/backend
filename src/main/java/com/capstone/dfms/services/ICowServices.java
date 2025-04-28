package com.capstone.dfms.services;

import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.HealthRecordEntity;
import com.capstone.dfms.requests.*;
import com.capstone.dfms.responses.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface ICowServices {
    CowResponse createCow(CowEntity request);
    CowResponse importSingleCow(CowImportSingleRequest cowImportSingleRequest);
    CowPenBulkResponse<CowResponse> createBulkCow(List<CowCreateRequest> requests);
    CowResponse updateCow(Long id, CowUpdateRequest request);
    void deleteCow(Long id);
    CowResponse getCowById(Long id);
    List<CowResponse> getAllCows();

    byte[] generateCowQRCode(Long cowId);

    CowPenBulkResponse<CowResponse> saveCowsFromExcel(MultipartFile file) throws IOException;
    BulkCowHealthRecordResponse getInformationFromExcel(MultipartFile file) throws IOException;

    BulkCreateCowResponse createInformation(BulkCowRequest request);
    Long getImportedTimes();

    List<CowWithFeedMealResponse> getCowsByArea(Long areaId);

    List<CowEntity> getCowsByAreaSimple(Long areaId);
    ByteArrayInputStream exportCowTemplate() throws IOException;

    List<CowInPenResponse> getCowsArea(Long areaId);
}
