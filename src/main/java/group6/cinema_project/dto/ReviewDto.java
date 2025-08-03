package group6.cinema_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Long id;
    private String user; // hoặc userId nếu cần
    private String comment;
    private Integer rating;
    private LocalDateTime date;
} 