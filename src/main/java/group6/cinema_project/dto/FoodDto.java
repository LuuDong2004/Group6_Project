package group6.cinema_project.dto;

import java.math.BigDecimal;

import group6.cinema_project.entity.Food;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FoodDto {
    private Integer id;
    @NotBlank(message = "Tên không được để trống.")
    @Size(max = 255, message = "Tên không được vượt quá 255 ký tự.")
    @Pattern(regexp = "^[a-zA-Z0-9\sÀ-ỹ]+$", message = "Tên không được chứa ký tự đặc biệt.")
    private String name;
    @NotNull(message = "Giá không được để trống.")
//    @Min(value = 1, message = "Giá phải lớn hơn 0.")
    private BigDecimal price;
    @NotNull(message = "Kích thước không được để trống.")
//    @Min(value = 1, message = "Kích thước phải lớn hơn 0.")
    private Integer size;
    private String description;
    private String image;

    public FoodDto() {
    }
    public FoodDto(Integer id, String name, BigDecimal price, int size, String description, String image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.size = size;
        this.description = description;
        this.image = image;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static FoodDto fromEntity(Food food) {
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

    public Food toEntity() {
        Food food = new Food();
        food.setId(this.id);
        food.setName(this.name);
        food.setPrice(this.price);
        food.setSize(this.size);
        food.setDescription(this.description);
        food.setImage(this.image);
        return food;
    }
}