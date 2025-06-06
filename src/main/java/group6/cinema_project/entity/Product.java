package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"comboItems", "itemInCombos", "bookingProducts"})
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "name", nullable = false, length = 150)
    private String name;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "image", length = 500)
    private String image;
    
    @Column(name = "type", length = 50)
    private String type = "Food";
    
    @OneToMany(mappedBy = "comboProduct", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ComboItem> comboItems;
    
    @OneToMany(mappedBy = "itemProduct", fetch = FetchType.LAZY)
    private List<ComboItem> itemInCombos;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookingProduct> bookingProducts;
} 