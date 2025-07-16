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


    // Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public CinemaChain getCinemaChain() {
        return cinemaChain;
    }
    public void setCinemaChain(CinemaChain cinemaChain) {
        this.cinemaChain = cinemaChain;
    }
}