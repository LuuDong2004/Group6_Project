package group6.cinema_project.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import group6.cinema_project.dto.FoodDto;
import group6.cinema_project.entity.Food;
import group6.cinema_project.repository.FoodRepository;
import group6.cinema_project.service.FoodService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {
    private final FoodRepository foodRepository;

    private FoodDto toDto(Food food) {
        if (food == null) return null;
        return new FoodDto(
                food.getId(),
                food.getName(),
                food.getPrice(),
                food.getSize(),
                food.getDescription(),
                food.getImage()
        );
    }

    private Food toEntity(FoodDto dto) {
        if (dto == null) return null;
        Food food = new Food();
        food.setId(dto.getId());
        food.setName(dto.getName());
        food.setPrice(dto.getPrice());
        food.setSize(dto.getSize());
        food.setDescription(dto.getDescription());
        food.setImage(dto.getImage());
        return food;
    }

    @Override
    public List<FoodDto> getAllFoods() {
        return foodRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public Page<FoodDto> getFoodsPage(int page, int size) {
        Page<Food> foodPage = foodRepository.findAll(PageRequest.of(page, size));
        List<FoodDto> foodDtos = foodPage.getContent().stream().map(this::toDto).collect(Collectors.toList());
        return new PageImpl<>(foodDtos, foodPage.getPageable(), foodPage.getTotalElements());
    }

    @Override
    public FoodDto getFoodById(Integer id) {
        Optional<Food> food = foodRepository.findById(id);
        return food.map(this::toDto).orElse(null);
    }

    @Override
    public FoodDto saveOrUpdate(FoodDto foodDto) {
        Food food = toEntity(foodDto);
        Food saved = foodRepository.save(food);
        return toDto(saved);
    }

    @Override
    public void deleteFood(Integer id) {
        foodRepository.deleteById(id);
    }

    // Trừ số lượng combo food khi user mua
    @Transactional
    public void subtractFoodQuantities(Map<Integer, Integer> foodOrder) {
        for (Map.Entry<Integer, Integer> entry : foodOrder.entrySet()) {
            Integer foodId = entry.getKey();
            Integer quantity = entry.getValue();
            Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Combo food not found"));
            if (food.getSize() < quantity) {
                throw new RuntimeException("Không đủ số lượng combo food: " + food.getName());
            }
            food.setSize(food.getSize() - quantity);
            foodRepository.save(food);
        }
    }
} 