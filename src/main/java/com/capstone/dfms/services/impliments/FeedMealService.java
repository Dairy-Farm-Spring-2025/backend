package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.exceptions.DataNotFoundException;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.components.utils.StringUtils;
import com.capstone.dfms.models.*;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.models.enums.FeedMealStatus;
import com.capstone.dfms.repositories.*;
import com.capstone.dfms.requests.FeedMealDetailRequest;
import com.capstone.dfms.requests.FeedMealRequest;
import com.capstone.dfms.requests.UpdateFeedMealRequest;
import com.capstone.dfms.responses.CalculateFoodResponse;
import com.capstone.dfms.services.IFeedMealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedMealService implements IFeedMealService {
    private final IFeedMealRepository feedMealRepository;
    private final IFeedMealDetailRepository feedMealDetailRepository;
    private final ICowTypeRepository cowTypeRepository;
    private final IItemRepository itemRepository;
    private final ICategoryRepository categoryRepository;
    private final IPenRepository penRepository;
    private final ICowPenRepository cowPenRepository;


    @Override
    public FeedMealEntity createFeedMeal(FeedMealRequest request) {
        CowTypeEntity cowTypeEntity = cowTypeRepository.findById(request.getCowTypeId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, LocalizationUtils.getMessage("cow_type.not_found")));

        boolean exists = feedMealRepository.existsByCowStatusAndCowTypeEntityAndStatus(
                request.getCowStatus(),
                cowTypeEntity,
                FeedMealStatus.inUse
        );

        if (exists) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    LocalizationUtils.getMessage("feed_meal.already_exists")
            );
        }

        Map<String, BigDecimal> requiredPercentages = Map.of(
                "Cỏ Khô", BigDecimal.valueOf(35.0),
                "Thức ăn tinh", BigDecimal.valueOf(25.0),
                "Thức ăn ủ chua", BigDecimal.valueOf(30.0),
                "Khoáng chất", BigDecimal.valueOf(10.0)
        );

        Map<String, BigDecimal> dryMatterRatios = Map.of(
                "Cỏ Khô", BigDecimal.valueOf(0.85),
                "Thức ăn tinh", BigDecimal.valueOf(0.90),
                "Thức ăn ủ chua", BigDecimal.valueOf(0.35),
                "Khoáng chất", BigDecimal.valueOf(0.75)
        );

        BigDecimal totalWeight = request.getDetails().stream()
                .map(FeedMealDetailRequest::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal totalDryMatter = BigDecimal.ZERO;

        for (var entry : requiredPercentages.entrySet()) {
            CategoryEntity category = categoryRepository.findAll().stream()
                    .filter(cat -> StringUtils.normalizeString(cat.getName()).equals(StringUtils.normalizeString(entry.getKey())))
                    .findFirst()
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Category '" + entry.getKey() + "' not found"));

            BigDecimal categoryWeight = getTotalWeightByCategory(request.getDetails(), category);
            BigDecimal percentage = calculatePercentage(categoryWeight, totalWeight);

            if (!isValidPercentage(percentage, entry.getValue())) {
                throw new AppException(HttpStatus.BAD_REQUEST,
                        String.format("Tỷ lệ %s không đúng, yêu cầu %.0f%%, hiện tại là %.2f%%.",
                                entry.getKey(), entry.getValue(), percentage));
            }

            BigDecimal dryMatterRatio = dryMatterRatios.getOrDefault(entry.getKey(), BigDecimal.ONE);
            totalDryMatter = totalDryMatter.add(categoryWeight.multiply(dryMatterRatio));
        }

        BigDecimal requiredDryMatter = BigDecimal.valueOf(calculateDryMatter(request.getCowStatus(), request.getCowTypeId()));

        if (!isValidPercentage(totalDryMatter, requiredDryMatter)) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    String.format("Tổng DM không phù hợp! Yêu cầu %.2f kg, hiện tại là %.2f kg.",
                            requiredDryMatter, totalDryMatter));
        }

        FeedMealEntity savedFeedMeal = feedMealRepository.save(
                FeedMealEntity.builder()
                        .name(request.getName())
                        .status(FeedMealStatus.inUse)
                        .cowStatus(request.getCowStatus())
//                        .shift(request.getShift())
                        .description(request.getDescription())
                        .cowTypeEntity(cowTypeEntity)
                        .build()
        );

        List<FeedMealDetailEntity> feedMealDetails = request.getDetails().stream()
                .map(detail -> FeedMealDetailEntity.builder()
                        .quantity(detail.getQuantity())
                        .itemEntity(itemRepository.findById(detail.getItemId())
                                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, LocalizationUtils.getMessage("item.not_exist"))))
                        .feedMealEntity(savedFeedMeal)
                        .build())
                .collect(Collectors.toList());

        feedMealDetailRepository.saveAll(feedMealDetails);
        return savedFeedMeal;
    }

    private boolean isValidPercentage(BigDecimal actual, BigDecimal expected) {
        BigDecimal difference = actual.subtract(expected).abs();
        BigDecimal tolerance = BigDecimal.valueOf(5.0);
        return difference.compareTo(tolerance) <= 0;
    }

    private BigDecimal getTotalWeightByCategory(List<FeedMealDetailRequest> details, CategoryEntity category) {
        return details.stream()
                .map(detail -> {
                    ItemEntity item = itemRepository.findById(detail.getItemId()).orElse(null);
                    return (item != null && item.getCategoryEntity().equals(category)) ? detail.getQuantity() : BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculatePercentage(BigDecimal part, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return part.multiply(BigDecimal.valueOf(100))
                .divide(total, 10, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);
    }


    @Override
    public double calculateDryMatter(CowStatus cowStatus, Long cowTypeId) {
        CowTypeEntity cowTypeEntity = cowTypeRepository.findById(cowTypeId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, LocalizationUtils.getMessage("cow_type.not_found")));
        double maxWeight = cowTypeEntity.getMaxWeight();
        double dryMatter = 0.0;

        switch(cowStatus) {
            case milkingCow:
                dryMatter = maxWeight * 0.04;
                break;
            case dryCow:
                dryMatter = maxWeight * 0.025;
                break;
            case youngCow:
                dryMatter = (maxWeight * 0.8) * 0.03;
                break;
            case sickCow:
                dryMatter = maxWeight * 0.02;
                break;
            case seriousSickcow:
                dryMatter = maxWeight * 0.015;
                break;
            default:
                throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("cow_status.invalid")
                );
        }

        return dryMatter;
    }

    @Override
    public FeedMealEntity getFeedMealById(long id) {
        return feedMealRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "feed_meal.not_existed"));
    }

    @Override
    public List<FeedMealEntity> getAllFeedMeals() {
        return feedMealRepository.findAll();
    }


    @Override
    public void deleteFeedMeal(long id) {
        FeedMealEntity vaccineCycle = feedMealRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "feed_meal.not_existed"));

        feedMealRepository.delete(vaccineCycle);
    }

    @Override
    public FeedMealEntity updateFeedMeal(Long feedMealId, UpdateFeedMealRequest request) {
        FeedMealEntity feedMeal = feedMealRepository.findById(feedMealId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, new AppException(HttpStatus.BAD_REQUEST, "feed_meal.not_existed")));

        if (request.getStatus() == FeedMealStatus.inUse) {
            boolean exists = feedMealRepository.existsByCowStatusAndCowTypeEntityAndStatus(
                    feedMeal.getCowStatus(),
                    feedMeal.getCowTypeEntity(),
                    FeedMealStatus.inUse
            );

            if (exists) {
                throw new AppException(HttpStatus.BAD_REQUEST,
                        LocalizationUtils.getMessage("feed_meal.already_exists"));
            }
        }

        if (request.getName() != null) {
            feedMeal.setName(request.getName());
        }
        if (request.getDescription() != null) {
            feedMeal.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            feedMeal.setStatus(request.getStatus());
        }

        return feedMealRepository.save(feedMeal);
    }

    @Override
    public FeedMealDetailEntity updateFeedMealDetail(Long feedMealDetailId, BigDecimal quantity) {
        FeedMealDetailEntity feedMealDetail = feedMealDetailRepository.findById(feedMealDetailId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy chi tiết khẩu phần ăn với ID: " + feedMealDetailId));

        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Số lượng phải lớn hơn 0");
        }

        feedMealDetail.setQuantity(quantity);
        return feedMealDetailRepository.save(feedMealDetail);
    }


    @Override
    public void addFeedMealDetail(Long feedMealId, FeedMealDetailRequest request) {
        FeedMealEntity feedMeal = feedMealRepository.findById(feedMealId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy khẩu phần ăn với ID: " + feedMealId));

        ItemEntity item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy nguyên liệu với ID: " + request.getItemId()));

        if (request.getQuantity() == null || request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Số lượng phải lớn hơn 0");
        }

        if (feedMealDetailRepository.findByFeedMealEntityAndItemEntity(feedMeal, item).isPresent()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Nguyên liệu này đã có trong khẩu phần ăn");
        }

        FeedMealDetailEntity detail = new FeedMealDetailEntity();
        detail.setFeedMealEntity(feedMeal);
        detail.setItemEntity(item);
        detail.setQuantity(request.getQuantity());

        feedMealDetailRepository.save(detail);
    }


    @Override
    public void removeFeedMealDetail(Long feedMealId) {
        FeedMealDetailEntity feedMealDetail = feedMealDetailRepository.findById(feedMealId).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy chi tiết bữa ăn"));
        feedMealDetailRepository.delete(feedMealDetail);
    }


    public List<CalculateFoodResponse> calculateFeedForArea(Long areaId) {
        List<PenEntity> pensInArea = penRepository.findByArea(areaId);
        Map<ItemEntity, BigDecimal> totalFeedRequired = new HashMap<>();

        for (PenEntity pen : pensInArea) {
            List<CowEntity> cowsInPen = cowPenRepository.findCowsByPenId(pen.getPenId());

            for (CowEntity cow : cowsInPen) {
                Optional<FeedMealEntity> feedMealOpt = feedMealRepository.findByCowTypeAndStatus(
                        cow.getCowTypeEntity(), cow.getCowStatus()
                );
                if (feedMealOpt.isEmpty()) {
                    throw new AppException(HttpStatus.BAD_REQUEST,"Không tìm thấy khẩu phần ăn cho loại bò: "
                            + cow.getCowTypeEntity().getName() + " với trạng thái " + cow.getCowStatus());
                }

                FeedMealEntity feedMeal = feedMealOpt.get();
                for (FeedMealDetailEntity detail : feedMeal.getFeedMealDetails()) {
                    ItemEntity item = detail.getItemEntity();
                    BigDecimal quantityRequired = detail.getQuantity();

                    totalFeedRequired.put(item, totalFeedRequired.getOrDefault(item, BigDecimal.ZERO).add(quantityRequired));
                }
            }
        }

        return totalFeedRequired.entrySet().stream()
                .map(entry -> new CalculateFoodResponse(
                        entry.getKey().getName(),
                        entry.getKey().getItemId(),
                        entry.getKey().getUnit(),
                        entry.getValue()
                ))
                .collect(Collectors.toList());
    }
}
