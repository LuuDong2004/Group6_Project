package group6.cinema_project.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "Food")
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name="name", nullable = false, length = 255)
    @NotBlank(message = "Tên món ăn không được để trống")
    @Size(min = 2, max = 100, message = "Tên món ăn phải từ 2-100 ký tự")
    private String name;

    @Column(name="price", precision = 18, scale = 2)
    @NotNull(message = "Giá không được để trống")
    @Min(value = 0, message = "Giá phải lớn hơn hoặc bằng 0")
    private BigDecimal price;

    @Column(name="size")
    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    private int size;

    @Column(name="description", columnDefinition = "nvarchar(max)")
    @NotBlank(message = "Mô tả không được để trống")
    @Size(min = 5, max = 500, message = "Mô tả phải từ 5-500 ký tự")
    private String description;

    @Column(name="image", length = 255)
    private String image;

    public Food() {
    }
    public Food(String name, BigDecimal price, int size, String description, String image) {
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