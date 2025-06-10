package group6.cinema_project.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Director")
public class Director {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String image;
    
    @Column(columnDefinition = "nvarchar(max)")
    private String description;

    // Constructor mặc định
    public Director() {}

    // Constructor với tham số
    public Director(String name, String image, String description) {
        this.name = name;
        this.image = image;
        this.description = description;
    }

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}