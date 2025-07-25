package group6.cinema_project.entity;

import jakarta.persistence.*;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "Branch")
@NoArgsConstructor
@Data
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false, length = 255, columnDefinition = "nvarchar(255)")
    private String name;

    @Column(name = "description",length = 255 , columnDefinition = "nvarchar(255)")
    private String description;

    @Column(name = "address", nullable = false, length = 255 , columnDefinition = "nvarchar(255)")
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_chain_id")
    private CinemaChain cinemaChain;

}
