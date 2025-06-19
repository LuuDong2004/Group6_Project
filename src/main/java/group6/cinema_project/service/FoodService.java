package group6.cinema_project.service;

import java.util.List;

import group6.cinema_project.dto.FoodDto;

public interface FoodService {
    FoodDto getFoodById(Integer id);
    FoodDto saveOrUpdate(FoodDto foodDto);
    void deleteFood(Integer id);
    List<FoodDto> getAllFoods();


}
