package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.exceptions.DataNotFoundException;
import com.capstone.dfms.components.utils.StringUtils;
import com.capstone.dfms.models.*;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.repositories.*;
import com.capstone.dfms.requests.FeedMealDetailRequest;
import com.capstone.dfms.requests.FeedMealRequest;
import com.capstone.dfms.services.IFeedMealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedMealService implements IFeedMealService {
    private final IFeedMealRepository feedMealRepository;
    private final IFeedMealDetailRepository feedMealDetailRepository;
    private final ICowTypeRepository cowTypeRepository;
    private final IItemRepository itemRepository;
    private final ICategoryRepository categoryRepository;


    @Override
    public FeedMealEntity createFeedMeal(FeedMealRequest request) {
        CowTypeEntity cowTypeEntity = cowTypeRepository.findById(request.getCowTypeId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Cow Type not found"));

        Map<String, Double> requiredPercentages = Map.of(
                "Cỏ Khô", 35.0,
                "Thức ăn tinh", 25.0,
                "Thức ăn ủ chua", 30.0,
                "Khoáng chất", 10.0
        );

        Map<String, Double> dryMatterRatios = Map.of(
                "Cỏ Khô", 0.85,
                "Thức ăn tinh", 0.90,
                "Thức ăn ủ chua", 0.35,
                "Khoáng chất", 0.75
        );

        double totalWeight = request.getDetails().stream()
                .mapToDouble(FeedMealDetailRequest::getQuantity)
                .sum();

        double totalDryMatter = 0.0;

        for (var entry : requiredPercentages.entrySet()) {
            CategoryEntity category = categoryRepository.findAll().stream()
                    .filter(cat -> StringUtils.normalizeString(cat.getName()).equals(StringUtils.normalizeString(entry.getKey())))
                    .findFirst()
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Category '" + entry.getKey() + "' not found"));

            double categoryWeight = getTotalWeightByCategory(request.getDetails(), category);
            double percentage = (categoryWeight / totalWeight) * 100;

            if (!isValidPercentage(percentage, entry.getValue())) {
                throw new AppException(HttpStatus.BAD_REQUEST,
                        String.format("Tỷ lệ %s không đúng, yêu cầu %.0f%%, hiện tại là %.2f%%.",
                                entry.getKey(), entry.getValue(), percentage));
            }

            double dryMatterRatio = dryMatterRatios.getOrDefault(entry.getKey(), 1.0);
            totalDryMatter += categoryWeight * dryMatterRatio;
        }

        double requiredDryMatter = calculateDryMatter(request.getCowStatus(), request.getCowTypeId());

        if (!isValidPercentage(totalDryMatter, requiredDryMatter)) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    String.format("Tổng DM không phù hợp! Yêu cầu %.2f kg, hiện tại là %.2f kg.",
                            requiredDryMatter, totalDryMatter));
        }

        FeedMealEntity savedFeedMeal = feedMealRepository.save(
                FeedMealEntity.builder()
                        .name(request.getName())
                        .cowStatus(request.getCowStatus())
                        .shift(request.getShift())
                        .description(request.getDescription())
                        .cowTypeEntity(cowTypeEntity)
                        .build()
        );

        List<FeedMealDetailEntity> feedMealDetails = request.getDetails().stream()
                .map(detail -> FeedMealDetailEntity.builder()
                        .quantity(detail.getQuantity())
                        .itemEntity(itemRepository.findById(detail.getItemId())
                                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Item not found")))
                        .feedMealEntity(savedFeedMeal)
                        .build())
                .collect(Collectors.toList());

        feedMealDetailRepository.saveAll(feedMealDetails);
        return savedFeedMeal;
    }

    private boolean isValidPercentage(double actual, double expected) {
        return Math.abs(actual - expected) <= 2.0;
    }

    private double getTotalWeightByCategory(List<FeedMealDetailRequest> details, CategoryEntity category) {
        return details.stream()
                .map(detail -> itemRepository.findById(detail.getItemId()).orElse(null))
                .filter(Objects::nonNull)
                .filter(item -> item.getCategoryEntity().equals(category))
                .mapToDouble(item -> details.stream()
                        .filter(detail -> detail.getItemId().equals(item.getItemId()))
                        .mapToDouble(FeedMealDetailRequest::getQuantity)
                        .sum())
                .sum();
    }


    @Override
    public FeedMealEntity getFeedMealById(long id) {
        return feedMealRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "This feed meal is not existed!"));
    }

    @Override
    public List<FeedMealEntity> getAllFeedMeals() {
        return feedMealRepository.findAll();
    }


    @Override
    public void deleteFeedMeal(long id) {
        FeedMealEntity vaccineCycle = feedMealRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Feed Meal", "id", id));

        feedMealRepository.delete(vaccineCycle);
    }

    @Override
    public double calculateDryMatter(CowStatus cowStatus, Long cowTypeId) {
        CowTypeEntity cowTypeEntity = cowTypeRepository.findById(cowTypeId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Cow Type not found"));
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
                throw new AppException(HttpStatus.BAD_REQUEST, "Invalid cow status");
        }

        return dryMatter;
    }





}
