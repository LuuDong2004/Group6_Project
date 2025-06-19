package group6.cinema_project.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import group6.cinema_project.service.FoodService;
import org.springframework.stereotype.Service;

import group6.cinema_project.dto.FoodDto;
import group6.cinema_project.entity.Food;
import group6.cinema_project.repository.FoodRepository;
import group6.cinema_project.service.IFoodService;
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
} 