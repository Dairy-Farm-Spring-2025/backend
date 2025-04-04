package com.capstone.dfms.services.impliments;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.utils.QRCodeUtil;
import com.capstone.dfms.components.utils.StringUtils;
import com.capstone.dfms.mappers.ICowMapper;
import com.capstone.dfms.mappers.IHealthReportMapper;
import com.capstone.dfms.mappers.IPenMapper;
import com.capstone.dfms.models.*;
import com.capstone.dfms.repositories.*;
import com.capstone.dfms.requests.CowCreateRequest;
import com.capstone.dfms.requests.CowExcelCreateRequest;
import com.capstone.dfms.requests.CowUpdateRequest;
import com.capstone.dfms.requests.HealthRecordExcelRequest;
import com.capstone.dfms.responses.BulkCowHealthRecordResponse;
import com.capstone.dfms.responses.CowHealthInfoResponse;
import com.capstone.dfms.responses.CowPenBulkResponse;
import com.capstone.dfms.responses.CowResponse;
import com.capstone.dfms.services.ICowServices;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CowServices implements ICowServices {
    private final ICowRepository cowRepository;
    private final ICowTypeRepository cowTypeRepository;
    private final ICowMapper cowMapper;
    private final IHealthRecordRepository healthRecordRepository;
    private final IIllnessRepository illnessRepository;
    private final ICowPenRepository cowPenRepository;
    private final IVaccineInjectionRepository vaccineInjectionRepository;
    private final IPenMapper penMapper;
    private final IHealthReportMapper healthReportMapper;



    @Override
    public CowResponse createCow(CowEntity request) {
        if (cowRepository.existsByName(request.getName())) {
            throw new AppException(HttpStatus.OK, "Cow with the name '" + request.getName() + "' already exists.");
        }

        CowTypeEntity cowType = cowTypeRepository.findById(request.getCowTypeEntity().getCowTypeId())
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Cow type not found."));
        request.setCowTypeEntity(cowType);

        if(request.getName() == null)
            request.setName(this.getInitials(cowType.getName()));

        CowEntity savedEntity = cowRepository.save(request);
        return cowMapper.toResponse(savedEntity);
    }

    @Override
    public CowPenBulkResponse<CowResponse> createBulkCow(List<CowCreateRequest> requests) {
        List<CowResponse> responses = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int rowNum = 1;
        for(CowCreateRequest request: requests){
            try{
                responses.add(createCow(cowMapper.toModel(request)));
            } catch (Exception ex){
                errors.add("Error at row " + rowNum+ ": " + ex);
            }
            rowNum++;
        }
        return new CowPenBulkResponse<>(responses, errors);
    }

    @Override
    public CowResponse updateCow(Long id, CowUpdateRequest request) {
        CowEntity existingEntity = cowRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Cow with ID '" + id + "' not found."));

        cowMapper.updateCowFromRequest(request, existingEntity);

        if(request.getCowTypeId() != null){
            CowTypeEntity cowType = cowTypeRepository.findById(request.getCowTypeId())
                    .orElseThrow(() -> new AppException(HttpStatus.OK, "Cow type not found."));
            existingEntity.setCowTypeEntity(cowType);
        }

        CowEntity updatedEntity = cowRepository.save(existingEntity);
        return getCowById(updatedEntity.getCowId());
    }

    @Override
    public void deleteCow(Long id) {
        CowEntity existingEntity = cowRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Cow with ID '" + id + "' not found."));
        cowRepository.delete(existingEntity);
    }

    @Override
    public CowResponse getCowById(Long id) {
        CowEntity cowEntity = cowRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Cow with ID '" + id + "' not found."));

        CowResponse response = cowMapper.toResponse(cowEntity);

        boolean isInPen = this.cowIsInPen(cowEntity.getCowId());
        response.setInPen(isInPen);

        CowPenEntity latestCowPen = cowPenRepository.latestCowPenByCowId(id);
        if (latestCowPen != null) {
            response.setPenResponse(penMapper.toResponse(latestCowPen.getPenEntity()));
        } else {
            response.setPenResponse(null);
        }
        response.setHealthInfoResponses(this.getAllHealthInfoOrderedDesc(id));
        Optional<HealthRecordEntity> latestHealthRecord = healthRecordRepository.findFirstByCowEntity_CowIdOrderByReportTimeDesc(id);

        if (latestHealthRecord.isPresent()) {
            HealthRecordEntity healthRecord = latestHealthRecord.get();
            response.setCowStatus(healthRecord.getPeriod());
            response.setSize(healthRecord.getSize());
        } else {
            response.setWeight(0.0f);
            response.setSize(0.0f);
        }

        return response;
    }

    @Override
    public List<CowResponse> getAllCows() {
        List<CowEntity> cowEntities = cowRepository.findAll();

        return cowEntities.stream()
                .map(cowEntity -> {
                    // Map CowEntity to CowResponse
                    CowResponse response = cowMapper.toResponse(cowEntity);

                    // Check if the cow is in a pen
                    boolean isInPen = this.cowIsInPen(cowEntity.getCowId());
                    response.setInPen(isInPen);

                    CowPenEntity latestCowPen = cowPenRepository.latestCowPenByCowId(cowEntity.getCowId());
                    if (latestCowPen != null) {
                        response.setPenResponse(penMapper.toResponse(latestCowPen.getPenEntity()));
                    } else {
                        response.setPenResponse(null);
                    }
                    List<CowHealthInfoResponse<?>> healthInfoResponses = this.getAllHealthInfoOrderedDesc(cowEntity.getCowId());
                    List<CowHealthInfoResponse<?>> healthInfoResponseTmp = new ArrayList<>();

                    // Set health information responses
                    if (healthInfoResponses.size() > 0){
                        healthInfoResponseTmp.add(healthInfoResponses.get(0));
                        response.setHealthInfoResponses(healthInfoResponseTmp);
                    }


                    // Fetch the latest health record
                    Optional<HealthRecordEntity> latestHealthRecord = healthRecordRepository.findFirstByCowEntity_CowIdOrderByReportTimeDesc(cowEntity.getCowId());

                    if (latestHealthRecord.isPresent()) {
                        // Set cow status, weight, and size from the latest health record
                        HealthRecordEntity healthRecord = latestHealthRecord.get();
                        response.setCowStatus(healthRecord.getPeriod());
                        response.setSize(healthRecord.getSize());
                    } else {
                        // Default values if no health record exists
                        response.setWeight(0.0f);
                        response.setSize(0.0f);
                    }

                    return response;
                })
                .toList();
    }

    //----------General Function---------------------------------------------------
    private String generateCowName() {
        long count = cowRepository.count();

        return "CO" + String.format("%03d", count + 1);
    }

    public String getInitials(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        // Split the string into words
        String[] words = input.trim().split("\\s+");

        // Extract the first character of each word
        StringBuilder initials = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                initials.append(word.charAt(0)); // Append the first character
            }
        }

        return initials.toString() + (cowRepository.countByNameContains(initials.toString()) + 1);
    }

    private boolean cowIsInPen(Long cowId){
        return !cowRepository.isCowNotInAnyPen(cowId, LocalDateTime.now());
    }

    private List<CowHealthInfoResponse<?>> getAllHealthInfoOrderedDesc(Long cowId) {
        // Fetch health records for the given cow
        List<HealthRecordEntity> healthRecords = healthRecordRepository.findByCowEntityCowId(cowId);
        // Fetch illnesses for the given cow
        List<IllnessEntity> illnesses = illnessRepository.findByCowEntityCowId(cowId);

        List<VaccineInjectionEntity> injectionEntities = vaccineInjectionRepository.findByCowEntity_CowId(cowId);

        // Create a list to hold the unified response objects
        List<CowHealthInfoResponse<?>> responses = new ArrayList<>();

        // Map health records to the response type
        for (HealthRecordEntity record : healthRecords) {
            // Convert the reportTime to LocalDate (assumes reportTime is not null)
            LocalDate reportDate = record.getReportTime().toLocalDate();
            CowHealthInfoResponse<HealthRecordEntity> response = CowHealthInfoResponse.<HealthRecordEntity>builder()
                    .id(record.getHealthRecordId())
                    .type("HEALTH_RECORD")
                    .date(reportDate)
                    .health(record)
                    .build();
            responses.add(response);
        }

        for (IllnessEntity illness : illnesses) {
            LocalDate startDate = illness.getStartDate();
            CowHealthInfoResponse<IllnessEntity> response = CowHealthInfoResponse.<IllnessEntity>builder()
                    .id(illness.getIllnessId())
                    .type("ILLNESS")
                    .date(startDate)
                    .health(illness)
                    .build();
            responses.add(response);
        }

        for (VaccineInjectionEntity injectionEntity : injectionEntities){
            CowHealthInfoResponse<VaccineInjectionEntity> response = CowHealthInfoResponse.<VaccineInjectionEntity>builder()
                    .id(injectionEntity.getId())
                    .type("INJECTIONS")
                    .date(injectionEntity.getInjectionDate())
                    .health(injectionEntity)
                    .build();
            responses.add(response);
        }

        // Ensure a global descending order by date, regardless of type.
        responses.sort(
                Comparator.comparing(
                        (CowHealthInfoResponse<?> r) -> r.getDate(),
                        Comparator.nullsLast(Comparator.naturalOrder())
                )
        );
        return responses;
    }


    //--------------------------------------------------------------------------------
    private <T> void updateField(Supplier<T> getter, Consumer<T> setter) {
        T value = getter.get();
        if (value != null) {
            setter.accept(value);
        }
    }




    @Override
    public byte[] generateCowQRCode(Long cowId) {
        CowEntity cow = cowRepository.findById(cowId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Cow not found"));

        String cowUrl = "http://localhost:5173/dairy/cow-management/" + cowId;
        try {
            return QRCodeUtil.generateQRCode(cowUrl, 300, 300);
        } catch (Exception e) {
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate QR code", e);
        }
    }

    @Override
    public CowPenBulkResponse<CowResponse> saveCowsFromExcel(MultipartFile file) throws IOException {
        List<CowExcelCreateRequest> cowList = new ArrayList<>();
        List<CowResponse> savedCows = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        EasyExcel.read(file.getInputStream(), CowExcelCreateRequest.class, new ReadListener<CowExcelCreateRequest>() {
            @Override
            public void invoke(CowExcelCreateRequest cow, AnalysisContext analysisContext) {
                cowList.add(cow);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                System.out.println("Excel parsing completed!");
            }
        })
        .sheet("Cow")
        .doRead();

        int rowNum = 2; // Excel starts at row 1
        for (CowExcelCreateRequest row : cowList) {
            try {
                CowEntity cowEntity = cowMapper.toModel(row); // ✅ Use Mapper
                CowResponse response = createCow(cowEntity);
                savedCows.add(response);
            } catch (AppException e) {
                errors.add("Error at row " + rowNum + ": " + e.getMessage());
            } catch (Exception e) {
                errors.add("Error at row " + rowNum + ": " + e.getMessage());
            }
            rowNum++;
        }

        return new CowPenBulkResponse<>(savedCows, errors);
    }

    @Override
    public BulkCowHealthRecordResponse getInformationFromExcel(MultipartFile file) throws IOException {
        CowPenBulkResponse<CowExcelCreateRequest> cowBulkResponse = this.getCowsFromExcel(file);
        CowPenBulkResponse<HealthRecordExcelRequest> healthRecordEntityBulkResponse = this.getHealthRecordFromExcel(file, cowBulkResponse.getSuccesses());

        return new BulkCowHealthRecordResponse(cowBulkResponse, healthRecordEntityBulkResponse);
    }

    public CowPenBulkResponse<CowExcelCreateRequest> getCowsFromExcel(MultipartFile file) throws IOException {
        List<CowExcelCreateRequest> cowList = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        EasyExcel.read(file.getInputStream(), CowExcelCreateRequest.class, new ReadListener<CowExcelCreateRequest>() {
            @Override
            public void invoke(CowExcelCreateRequest cow, AnalysisContext analysisContext) {
                cowList.add(cow);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                System.out.println("Excel parsing completed!");
            }
        })
        .sheet("Cow")
        .doRead();

        for (CowExcelCreateRequest row : cowList) {
            try {
                CowEntity cowEntity = cowMapper.toModel(row);
                if(cowEntity.getCowTypeEntity() != null){
                    CowTypeEntity cowType = cowTypeRepository.findByName(cowEntity.getCowTypeEntity().getName())
                            .orElseThrow(
                                    () -> new AppException(HttpStatus.BAD_REQUEST,
                                            "Cow type name: " + cowEntity.getCowTypeEntity().getName() + " does not exist!"));
                    cowEntity.setCowTypeEntity(cowType);
                }
            } catch (AppException e) {
                errors.add("Error at row cow" + row.getName() + ": " + e.getMessage());
            } catch (Exception e) {
                errors.add("Error at row cow" + row.getName() + ": " + e.getMessage());
            }
        }

        return new CowPenBulkResponse<>(cowList, errors);
    }

    public CowPenBulkResponse<HealthRecordExcelRequest> getHealthRecordFromExcel(MultipartFile file, List<CowExcelCreateRequest> cowList) throws IOException {
        List<HealthRecordExcelRequest> healthRecordsList = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // Read Excel and store records in the list
        EasyExcel.read(file.getInputStream(), HealthRecordExcelRequest.class, new ReadListener<HealthRecordExcelRequest>() {
            @Override
            public void invoke(HealthRecordExcelRequest record, AnalysisContext context) {
                healthRecordsList.add(record);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                System.out.println("Excel parsing completed!");
            }
        })
        .sheet("Health Record") // Read the first sheet
        .doRead();

        int rowNum = 2; // Excel row numbering (assuming 1-based index with headers)
        for (HealthRecordExcelRequest row : healthRecordsList) {
            try {
                if(row.getCowName() == null){
                    throw new AppException(HttpStatus.BAD_REQUEST, "There is no name!");
                }
                if(cowList.stream()
                        .filter(cow -> cow.getName().equals(row.getCowName()))
                        .findFirst().isEmpty()){
                    throw new AppException(HttpStatus.BAD_REQUEST, "Health record of cow " + row.getCowName() + " is not valid");
                }

            } catch (AppException e) {
                errors.add("Error at row " + rowNum + ": " + e.getMessage());
            } catch (Exception e) {
                errors.add("Error at row " + rowNum + ": " + e.getMessage());
            }
            rowNum++;
        }

        return new CowPenBulkResponse<>(healthRecordsList, errors);

    }
}
