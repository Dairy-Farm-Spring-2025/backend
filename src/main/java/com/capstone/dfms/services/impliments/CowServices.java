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
import com.capstone.dfms.models.enums.CowOrigin;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.models.enums.Gender;
import com.capstone.dfms.models.enums.HealthRecordStatus;
import com.capstone.dfms.repositories.*;
import com.capstone.dfms.requests.*;
import com.capstone.dfms.responses.*;
import com.capstone.dfms.services.ICowServices;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();
    private final IPenRepository penRepository;
    private final IFeedMealRepository feedMealRepository;



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

        if(request.getCowStatus() != null){
            existingEntity.setCowStatus(request.getCowStatus());
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

        return initials.toString()+ "-0000-" + String.format("%04d", (cowRepository.countByNameContains(initials.toString() + "-0000-") + 1));
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
//        Long importTimes = Long.parseLong(this.getCellFromImportTimeA2(file));
//        Long maxImportTimes = cowRepository.getMaxImportTimes() == null ? 1 : cowRepository.getMaxImportTimes() + 1;
//        if(maxImportTimes != importTimes){
//            throw new AppException(HttpStatus.BAD_REQUEST, "Invalid import times");
//        }

        BulkResponse<CowExcelCreateRequest> cowBulkResponse = this.getCowsFromExcel(file);

        List<CowExcelCreateRequest> allCows = new ArrayList<>();
        allCows.addAll(cowBulkResponse.getSuccesses());
        allCows.addAll(cowBulkResponse.getErrors());

        BulkResponse<HealthRecordExcelRequest> healthRecordEntityBulkResponse = this.getHealthRecordFromExcel(file, allCows);

        return new BulkCowHealthRecordResponse(cowBulkResponse, healthRecordEntityBulkResponse);
    }

    @Override
    public BulkCreateCowResponse createInformation(BulkCowRequest request) {
        Long importTimes = cowRepository.getMaxImportTimes() == null ? 1 : cowRepository.getMaxImportTimes() + 1;

        BulkCreateCowResponse response = new BulkCreateCowResponse();
        List<CowResponse> cowEntities = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        request.getCows().forEach((cow) -> {
            try {
                if(Long.parseLong(cow.getName().split("-")[1]) != importTimes){
                    throw new AppException(HttpStatus.BAD_REQUEST, "Invalid name!");
                }

                CowEntity cowEntity = cowMapper.toModel(cow);
                if (cowEntity.getCowTypeEntity() != null) {
                    CowTypeEntity cowType = cowTypeRepository.findByName(cowEntity.getCowTypeEntity().getName())
                            .orElseThrow(
                                    () -> new AppException(HttpStatus.BAD_REQUEST,
                                            "Cow type name: " + cowEntity.getCowTypeEntity().getName() + " does not exist!"));
                    cowEntity.setCowTypeEntity(cowType);
                } else {
                    throw new AppException(HttpStatus.BAD_REQUEST, "Cow type is required!");
                }
                cowEntity.setImportTimes(importTimes);

                cowEntities.add(createCow(cowEntity));
            }
            catch (Exception ex){
                errors.add("Error at row cow" + cow.getName() + ": " + ex.getMessage());
            }
        });
        response.setCowsResponse(new CowPenBulkResponse<>(cowEntities, errors));

        List<HealthRecordEntity> healthRecordEntities = new ArrayList<>();
        List<String> errors2 = new ArrayList<>();
        request.getHealthRecords().forEach((record) -> {
            try {
                CowEntity cowEntity = cowRepository.findByName(record.getCowName())
                        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Invalid Cow name"));

                HealthRecordEntity entity = healthReportMapper.toModel(record);

                entity.setCowEntity(cowEntity);
                entity.setWeight(90 * (record.getChestCircumference() * record.getChestCircumference() * record.getBodyLength()));

                healthRecordEntities.add(healthRecordRepository.save(entity));
            }
            catch (Exception ex){
                errors2.add("Error at row cow" + record.getCowName() + ": " + ex.getMessage());
            }
        });
        response.setHealthRecordsResponse(new CowPenBulkResponse<>(healthRecordEntities, errors2));

        return response;
    }

    @Override
    public Long getImportedTimes() {
        return cowRepository.getMaxImportTimes() == null ? 0 : cowRepository.getMaxImportTimes();
    }

    public String getCellFromImportTimeA2(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheet("Import time");
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet 'Import time' not found.");
            }

            Row row = sheet.getRow(1); // A2 is row 1 (0-based index)
            if (row == null) {
                throw new IllegalArgumentException("Row 2 not found in 'Import time' sheet.");
            }

            Cell cell = row.getCell(0); // A column
            if (cell == null) {
                throw new IllegalArgumentException("Cell A2 not found in 'Import time' sheet.");
            }

            // Support both numeric and string values gracefully
            return switch (cell.getCellType()) {
                case NUMERIC -> String.valueOf((long) cell.getNumericCellValue()); // assume integer importTimes
                case STRING -> cell.getStringCellValue();
                default -> throw new IllegalArgumentException("Unsupported cell type in A2: " + cell.getCellType());
            };

        } catch (Exception e) {
            throw new IOException("Failed to read Import Time from Excel: " + e.getMessage(), e);
        }
    }



    public BulkResponse<CowExcelCreateRequest> getCowsFromExcel(MultipartFile file) throws IOException {
        List<CowExcelCreateRequest> cowList = new ArrayList<>();
        List<CowExcelCreateRequest> errors = new ArrayList<>();

        EasyExcel.read(file.getInputStream(), CowExcelCreateRequest.class, new ReadListener<CowExcelCreateRequest>() {
            @Override
            public void invoke(CowExcelCreateRequest cow, AnalysisContext context) {
                Set<ConstraintViolation<CowExcelCreateRequest>> violations = validator.validate(cow);
                if (!violations.isEmpty()) {
                    // Optionally, add validation messages to the object (you could add a `List<String> errors` field in DTO)
                    // Collect messages into a string or list
                    List<String> messages = violations.stream()
                            .map(ConstraintViolation::getMessage)
                            .toList(); // Or use newline "\n"

                    cow.setErrorStrings(messages); // Or cow.getErrors().addAll(...);
                    errors.add(cow);
                } else {
                    cowList.add(cow);
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                System.out.println("Excel parsing completed!");
            }
        }).sheet("Cow Import").doRead();

        // Validate reference data (like cow type)
        List<CowExcelCreateRequest> validCows = new ArrayList<>();
        for (CowExcelCreateRequest row : cowList) {
            try {
                CowEntity cowEntity = cowMapper.toModel(row);

                if (cowEntity.getCowTypeEntity() != null) {
                    CowTypeEntity cowType = cowTypeRepository.findByName(cowEntity.getCowTypeEntity().getName())
                            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST,
                                    "Cow type name: " + cowEntity.getCowTypeEntity().getName() + " does not exist!"));
                    cowEntity.setCowTypeEntity(cowType);
                } else {
                    throw new AppException(HttpStatus.BAD_REQUEST, "Cow type is required.");
                }
                if (cowEntity.getDateOfBirth().plusMonths(10).isAfter(LocalDate.now())
                        && cowEntity.getCowStatus().equals(CowStatus.milkingCow)){
                    throw new AppException(HttpStatus.BAD_REQUEST, "Milking cow start 10 months");
                }

                // If all is good, you can add to final list or save
                validCows.add(row);

            } catch (Exception e) {
                // Optionally log or wrap the error message
                row.setErrorStrings(new ArrayList<>(List.of(e.getMessage())));
                errors.add(row); // Or store a wrapper object with error message
            }
        }

        return BulkResponse.<CowExcelCreateRequest>builder()
                .successes(validCows)
                .errors(errors)
                .build();
    }


    public BulkResponse<HealthRecordExcelRequest> getHealthRecordFromExcel(MultipartFile file, List<CowExcelCreateRequest> cowList) throws IOException {
        List<HealthRecordExcelRequest> validRecords = new ArrayList<>();
        List<HealthRecordExcelRequest> errorRecords = new ArrayList<>();

        EasyExcel.read(file.getInputStream(), HealthRecordExcelRequest.class, new ReadListener<HealthRecordExcelRequest>() {
            @Override
            public void invoke(HealthRecordExcelRequest record, AnalysisContext analysisContext) {
                Set<ConstraintViolation<HealthRecordExcelRequest>> violations = validator.validate(record);
                if (!violations.isEmpty()) {
                    // Collect messages into a string or list
                    List<String> messages = violations.stream()
                            .map(ConstraintViolation::getMessage)
                            .toList(); // Or use newline "\n"

                    record.setErrorString(messages); // Or cow.getErrors().addAll(...);
                    errorRecords.add(record);
                } else {
                    validRecords.add(record);
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                System.out.println("Excel parsing completed!");
            }
        }).sheet("Health Record Import").doRead();

        int rowNum = 2; // assuming header is row 1
        List<HealthRecordExcelRequest> finalValidRecords = new ArrayList<>();

        for (HealthRecordExcelRequest record : validRecords) {
            try {
                if (record.getCowName() == null) {
                    throw new AppException(HttpStatus.BAD_REQUEST, "Cow name is missing");
                }

                boolean cowExists = cowList.stream()
                        .anyMatch(cow -> cow.getName().equals(record.getCowName()));

                if (!cowExists) {
                    throw new AppException(HttpStatus.BAD_REQUEST, "Cow '" + record.getCowName() + "' does not exist in cow list.");
                }

                finalValidRecords.add(record); // only add if valid cow name

            } catch (Exception e) {
                record.setErrorString(new ArrayList<>(List.of(e.getMessage())));
                errorRecords.add(record);
            }

            rowNum++;
        }

        return BulkResponse.<HealthRecordExcelRequest>builder()
                .successes(finalValidRecords)
                .errors(errorRecords)
                .build();
    }



    @Override
    public List<CowWithFeedMealResponse> getCowsByArea(Long areaId) {
        List<PenEntity> pens = penRepository.findByArea(areaId);

        if (pens.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> penIds = pens.stream()
                .map(PenEntity::getPenId)
                .collect(Collectors.toList());

        List<CowPenEntity> activeCowPens = cowPenRepository.findActiveByPenIds(penIds);

        List<CowWithFeedMealResponse> result = new ArrayList<>();

        for (CowPenEntity cowPen : activeCowPens) {
            CowEntity cow = cowPen.getCowEntity();

            Optional<FeedMealEntity> mealOpt = feedMealRepository
                    .findByCowTypeAndStatus(
                            cow.getCowTypeEntity(),
                            cow.getCowStatus());

            List<FeedMealCowResponse> mealDetails = mealOpt.map(meal ->
                    meal.getFeedMealDetails().stream()
                            .map(detail -> {
                                FeedMealCowResponse dto = new FeedMealCowResponse();
                                dto.setItemName(detail.getItemEntity().getName());
                                dto.setQuantity(detail.getQuantity());
                                return dto;
                            }).collect(Collectors.toList())
            ).orElse(Collections.emptyList());

            CowWithFeedMealResponse cowDTO = new CowWithFeedMealResponse();
            cowDTO.setCowId(cow.getCowId());
            cowDTO.setName(cow.getName());
            cowDTO.setCowStatus(cow.getCowStatus());
            cowDTO.setCowType(cow.getCowTypeEntity().getName());

            cowDTO.setPenId(cowPen.getPenEntity().getPenId());
            cowDTO.setPenName(cowPen.getPenEntity().getName());

            cowDTO.setFeedMeals(mealDetails);
            result.add(cowDTO);
        }
        result.sort(Comparator
                .comparing((CowWithFeedMealResponse c) -> c.getPenName().substring(0, 1))
                .thenComparing(c -> Integer.parseInt(c.getPenName().substring(1)))
        );
        return result;
    }


    @Override
    public List<CowEntity> getCowsByAreaSimple(Long areaId) {
        List<PenEntity> pens = penRepository.findByArea(areaId);
        if (pens.isEmpty()) return Collections.emptyList();

        List<Long> penIds = pens.stream().map(PenEntity::getPenId).toList();

        List<CowPenEntity> cowPens = cowPenRepository.findActiveByPenIds(penIds);

        return cowPens.stream()
                .map(CowPenEntity::getCowEntity)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public ByteArrayInputStream exportCowTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            int NUM_START = 1;
            int NUM_ROWS = 100;
            this.generateCowImport(workbook, NUM_START, NUM_ROWS);
            this.generateHealthRecordImport(workbook, NUM_START, NUM_ROWS);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void generateCowImport(Workbook workbook, int NUM_START, int NUM_ROWS) throws Exception {
        try{
            Sheet sheet = workbook.createSheet("Cow Import");

            String[] columns = {"Name", "Cow Status", "Date of Birth", "Date of Enter", "Cow Origin", "Gender", "Cow Type", "Description"};

            // Create shared styles
            CreationHelper creationHelper = workbook.getCreationHelper();
            CellStyle borderStyle = createBorderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook, creationHelper);
            CellStyle headerStyle = createHeaderStyle(workbook, borderStyle);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);

            // Create header row
            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            long importTime = cowRepository.getMaxImportTimes() + 1;
            List<String> cowTypes = cowTypeRepository.findAll().stream().map(CowTypeEntity::getName).toList();

            for (int i = 1; i <= NUM_ROWS; i++) {
                Row row = sheet.createRow(i);
                addFormatName(row, i + 1, importTime); // Excel row index is 1-based

                for (int j = 0; j < columns.length; j++) {
                    Cell cell = row.getCell(j);
                    if (cell == null) cell = row.createCell(j);

                    if (j == 2 || j == 3) {
                        cell.setCellStyle(dateStyle);
                    } else {
                        cell.setCellStyle(borderStyle);
                    }
                }
            }

            // Apply drop-downs
            addDropDownList(sheet, CowStatus.values(), NUM_START, NUM_ROWS, 1);
            addDropDownList(sheet, CowOrigin.values(), NUM_START, NUM_ROWS, 4);
            addDropDownList(sheet, Gender.values(), NUM_START, NUM_ROWS, 5);
            addDropDownList(sheet, cowTypes.toArray(new String[0]), NUM_START, NUM_ROWS, 6);

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
        } catch (Exception ex){
            throw new Exception();
        }
    }

    private void generateHealthRecordImport(Workbook workbook, int NUM_START, int NUM_ROWS) throws Exception {
        try {
            Sheet sheet = workbook.createSheet("Health Record Import");


            String[] columns = {
                        "Cow Name", "Status", "Size", "Period",
                        "Body Temperature", "Heart Rate", "Respiratory Rate",
                        "Ruminate Activity", "Chest Circumference", "Body Length", "Description"
            };

            // Create shared styles
            CellStyle borderStyle = createBorderStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook, borderStyle);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);

            // Create header row
            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fill sample rows
            // Fill sample rows
            for (int i = 1; i <= NUM_ROWS; i++) {
                Row row = sheet.createRow(i);

                for (int j = 0; j < columns.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellStyle(borderStyle);

                    // Set Cow Name with formula from 'Cow Import' sheet
                    if (j == 0) {
                        String formula = String.format("'Cow Import'!A%d", i + 1); // i+1 because row index is 1-based in Excel
                        cell.setCellFormula(formula);
                    }
                }
            }


            DataValidationHelper helper = sheet.getDataValidationHelper();
            DataValidationConstraint cowNameConstraint = helper.createFormulaListConstraint("CowNames");
            CellRangeAddressList cowNameAddress = new CellRangeAddressList(NUM_START, NUM_ROWS, 0, 0);
            sheet.addValidationData(helper.createValidation(cowNameConstraint, cowNameAddress));

            // Drop-downs for Status and Period
            addDropDownList(sheet, HealthRecordStatus.values(), NUM_START, NUM_ROWS, 1); // Status
            addDropDownList(sheet, CowStatus.values(), NUM_START, NUM_ROWS, 3);          // Period

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
        } catch (Exception e) {
            throw new Exception("Failed to generate health record import sheet", e);
        }
    }



    // Generic drop-down method
    private void addDropDownList(Sheet sheet, Object[] options, int rowStart, int rowEnd, int colIndex) {
        addDropDownList(sheet, Arrays.stream(options).map(Object::toString).toArray(String[]::new), rowStart, rowEnd, colIndex);
    }

    private void addDropDownList(Sheet sheet, String[] options, int rowStart, int rowEnd, int colIndex) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createExplicitListConstraint(options);
        CellRangeAddressList addressList = new CellRangeAddressList(rowStart, rowEnd, colIndex, colIndex);
        DataValidation validation = helper.createValidation(constraint, addressList);
        validation.setSuppressDropDownArrow(true);
        sheet.addValidationData(validation);
    }

    // Add dynamic formula to generate cow name
    private void addFormatName(Row row, int excelRow, long importTimes) {
        Cell nameCell = row.createCell(0); // Column A
        String formula = String.format(
                "IF(G%d<>\"\",UPPER(LEFT(G%d,1))&\"-%04d\"&\"-\"&TEXT(COUNTIF($G$2:G%d,G%d),\"0000\"),\"\")",
                excelRow, excelRow, importTimes, excelRow, excelRow
        );
        nameCell.setCellFormula(formula);
    }

    private CellStyle createBorderStyle(Workbook workbook) {
        CellStyle borderStyle = workbook.createCellStyle();
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setBorderBottom(BorderStyle.THIN);
        borderStyle.setBorderLeft(BorderStyle.THIN);
        borderStyle.setBorderRight(BorderStyle.THIN);
        return borderStyle;
    }

    private CellStyle createDateStyle(Workbook workbook, CreationHelper creationHelper) {
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.cloneStyleFrom(createBorderStyle(workbook));
        dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-mm-dd"));
        return dateStyle;
    }

    private CellStyle createHeaderStyle(Workbook workbook, CellStyle borderStyle) {
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.cloneStyleFrom(borderStyle);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFont(headerFont);
        return headerStyle;
    }




}
