package group6.cinema_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonSimpleDto {
    private Integer id;
    private String name;
    private String imageUrl;
} 