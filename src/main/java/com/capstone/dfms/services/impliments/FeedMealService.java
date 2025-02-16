package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.exceptions.DataNotFoundException;
import com.capstone.dfms.models.*;
import com.capstone.dfms.repositories.ICowTypeRepository;
import com.capstone.dfms.repositories.IFeedMealDetailRepository;
import com.capstone.dfms.repositories.IFeedMealRepository;
import com.capstone.dfms.repositories.IItemRepository;
import com.capstone.dfms.requests.FeedMealRequest;
import com.capstone.dfms.services.IFeedMealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedMealService implements IFeedMealService {
    private final IFeedMealRepository feedMealRepository;
    private final IFeedMealDetailRepository feedMealDetailRepository;
    private final ICowTypeRepository cowTypeRepository;
    private final IItemRepository itemRepository;


    @Override
    public FeedMealEntity createFeedMeal(FeedMealRequest request) {
        CowTypeEntity cowTypeEntity = cowTypeRepository.findById(request.getCowTypeId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Cow Type not found"));


        FeedMealEntity feedMealEntity = FeedMealEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .cowTypeEntity(cowTypeEntity)
                .build();

        FeedMealEntity saveFeedMeal = feedMealRepository.save(feedMealEntity);

        List<FeedMealDetailEntity> feedMealDetails = request.getDetails().stream().map(detail -> {
            ItemEntity itemEntity = itemRepository.findById(detail.getItemId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Item not found"));

            return FeedMealDetailEntity.builder()
                    .quantity(detail.getQuantity())
                    .itemEntity(itemEntity)
                    .shift(detail.getShift())
                    .feedMealEntity(saveFeedMeal)
                    .build();
        }).collect(Collectors.toList());

        feedMealDetailRepository.saveAll(feedMealDetails);

        return feedMealEntity;
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

}
