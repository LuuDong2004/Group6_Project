package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "actor") // Cập nhật tên bảng
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"movies"})
@EqualsAndHashCode(exclude = {"movies"})
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "image", length = 255) // Cập nhật tên cột
    private String imageUrl; // Cập nhật tên trường

    @Lob
    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)") // columnDefinition có thể cần thiết cho NVARCHAR(MAX) trên một số DB
    private String description;

    @ManyToMany(mappedBy = "actors")
    private Set<Movie> movies;
}
