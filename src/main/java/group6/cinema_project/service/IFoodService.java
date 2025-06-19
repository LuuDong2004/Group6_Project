package group6.cinema_project.service;

import java.util.List;

import group6.cinema_project.dto.FoodDto;

public interface IFoodService {
    List<FoodDto> getAllFoods();
    FoodDto getFoodById(Integer id);
    FoodDto saveOrUpdate(FoodDto foodDto);
    void deleteFood(Integer id);
} 