package group6.cinema_project.service.Admin.impl;

import group6.cinema_project.dto.FoodDto;
import group6.cinema_project.entity.Food;

import group6.cinema_project.repository.Admin.AdminFoodRepository;
import group6.cinema_project.service.Admin.IAdminFoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminFoodServiceImpl implements IAdminFoodService {

    @Autowired
    private AdminFoodRepository foodRepository;

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

    // Hàm mới hỗ trợ search và sort
    @Override
    public Page<FoodDto> getFoodsPage(int page, int size, String search, String sort) {
        Page<Food> foodPage;
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        boolean isSearch = search != null && !search.trim().isEmpty();
        boolean isSortAsc = "asc".equalsIgnoreCase(sort);
        boolean isSortDesc = "desc".equalsIgnoreCase(sort);
        if (isSearch && isSortAsc) {
            foodPage = foodRepository.searchByNameOrderByPriceAsc(search, pageable);
        } else if (isSearch && isSortDesc) {
            foodPage = foodRepository.searchByNameOrderByPriceDesc(search, pageable);
        } else if (isSearch) {
            foodPage = foodRepository.searchByName(search, pageable);
        } else if (isSortAsc) {
            foodPage = foodRepository.findAllOrderByPriceAsc(pageable);
        } else if (isSortDesc) {
            foodPage = foodRepository.findAllOrderByPriceDesc(pageable);
        } else {
            foodPage = foodRepository.findAll(pageable);
        }
        java.util.List<FoodDto> foodDtos = foodPage.getContent().stream().map(this::toDto).collect(java.util.stream.Collectors.toList());
        return new org.springframework.data.domain.PageImpl<>(foodDtos, foodPage.getPageable(), foodPage.getTotalElements());
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

    @Override
    public boolean isNameDuplicate(String name, Integer id) {
        List<Food> foods = foodRepository.findByName(name);
        if (id == null) {
            return !foods.isEmpty();
        } else {
            return foods.stream().anyMatch(f -> f.getId() != id);
        }
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
