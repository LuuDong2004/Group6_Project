package group6.cinema_project.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Actor") // <-- đúng tên bảng trong SQL
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String image;

    @Column(columnDefinition = "nvarchar(max)")
    private String description;

    // Default constructor
    public Actor() {
    }

    // Constructor with parameters
    public Actor(String name, String image, String description) {
        this.name = name;
        this.image = image;
        this.description = description;
    }

    // Getter and Setter methods
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

    // toString method for debugging
    @Override
    public String toString() {
        return "Actor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    // equals and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Actor actor = (Actor) o;
        return id != null && id.equals(actor.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}