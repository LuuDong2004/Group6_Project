package group6.cinema_project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "director") // Cập nhật tên bảng
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "movies" })
@EqualsAndHashCode(exclude = { "movies" })
public class Director {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, length = 255, unique = true)
    private String name;

    @Column(name = "image", length = 255) // Cập nhật tên cột
    private String imageUrl; // Cập nhật tên trường

    @Lob
    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)") // columnDefinition có thể cần thiết cho
                                                                      // NVARCHAR(MAX) trên một số DB
    private String description;

    @JsonIgnore
    @ManyToMany(mappedBy = "directors")
    private Set<Movie> movies;
}
