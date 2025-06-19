package group6.cinema_project.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class FoodDto {
    private Integer id;
    private String name;
    private BigDecimal price;
    private int size;
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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
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
}