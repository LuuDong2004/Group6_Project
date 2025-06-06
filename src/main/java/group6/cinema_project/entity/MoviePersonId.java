package group6.cinema_project.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoviePersonId implements Serializable {
    private Integer movie;
    private Integer person;
    private String roleType;
} 