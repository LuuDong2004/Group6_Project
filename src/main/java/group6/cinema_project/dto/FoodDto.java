
package group6.cinema_project.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FoodDto {
    private Integer id;
    
    @NotBlank(message = "Tên món ăn không được để trống")
    @Size(min = 2, max = 100, message = "Tên món ăn phải từ 2-100 ký tự")
    private String name;
    
    @NotNull(message = "Giá không được để trống")
    @Min(value = 0, message = "Giá phải lớn hơn hoặc bằng 0")
    private BigDecimal price;
    
    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    private Integer size;
    
    @NotBlank(message = "Mô tả không được để trống")
    @Size(min = 5, max = 500, message = "Mô tả phải từ 5-500 ký tự")
    private String description;
    
    private String image;
}
