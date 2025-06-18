package group6.cinema_project.dto;

import lombok.Data;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Data
@NoArgsConstructor
@Setter
@Getter

public class ActorDto {
    private Integer id;
    private String name;
    private String imageUrl;
    private String description;
}