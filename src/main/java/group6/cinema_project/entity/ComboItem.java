package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "combo_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ComboItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "combo_product_id", nullable = false)
    private Product comboProduct;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_product_id", nullable = false)
    private Product itemProduct;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;
} 