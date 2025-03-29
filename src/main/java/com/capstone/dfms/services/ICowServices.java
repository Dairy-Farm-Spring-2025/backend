package com.capstone.dfms.services;

import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.requests.CowExcelCreateRequest;
import com.capstone.dfms.requests.CowUpdateRequest;
import com.capstone.dfms.responses.CowPenBulkResponse;
import com.capstone.dfms.responses.CowResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ICowServices {
    CowResponse createCow(CowEntity request);
    CowResponse updateCow(Long id, CowUpdateRequest request);
    void deleteCow(Long id);
    CowResponse getCowById(Long id);
    List<CowResponse> getAllCows();

    byte[] generateCowQRCode(Long cowId);

    public CowPenBulkResponse<CowResponse> saveCowsFromExcel(MultipartFile file) throws IOException;
    public CowPenBulkResponse<CowResponse> getCowsFromExcel(MultipartFile file) throws IOException;
}
