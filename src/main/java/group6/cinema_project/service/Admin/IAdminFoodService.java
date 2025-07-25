package group6.cinema_project.service.Admin;

import group6.cinema_project.dto.FoodDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IAdminFoodService {
    FoodDto getFoodById(Integer id);
    FoodDto saveOrUpdate(FoodDto foodDto);
    void deleteFood(Integer id);
    List<FoodDto> getAllFoods();
    // Thêm phương thức phân trang, tìm kiếm, sắp xếp
    Page<FoodDto> getFoodsPage(int page, int size, String search, String sort);
    boolean isNameDuplicate(String name, Integer id);

}
