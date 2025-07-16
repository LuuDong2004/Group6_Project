package group6.cinema_project.service;

import java.util.List;

import group6.cinema_project.dto.FoodDto;

public interface FoodService {
    FoodDto getFoodById(Integer id);
    FoodDto saveOrUpdate(FoodDto foodDto);
    void deleteFood(Integer id);
    List<FoodDto> getAllFoods();
    // Thêm phương thức phân trang
    org.springframework.data.domain.Page<FoodDto> getFoodsPage(int page, int size);


}
