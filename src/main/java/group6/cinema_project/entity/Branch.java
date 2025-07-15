package group6.cinema_project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Branch")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;
 
}