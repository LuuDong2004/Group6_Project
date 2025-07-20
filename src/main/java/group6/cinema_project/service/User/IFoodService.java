package group6.cinema_project.service.User;

import group6.cinema_project.dto.FoodDto;

import java.util.List;

public interface IFoodService {
    List<FoodDto> getAllFoods();
    FoodDto getFoodById(Integer id);
    FoodDto saveOrUpdate(FoodDto foodDto);
    void deleteFood(Integer id);
}
