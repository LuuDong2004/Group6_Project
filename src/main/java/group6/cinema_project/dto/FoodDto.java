
package group6.cinema_project.dto;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FoodDto {
    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer size;
    private String description;
    private String image;
}
