package group6.cinema_project.config;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Director;
import group6.cinema_project.entity.Movie;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Configure ModelMapper to use strict matching strategy to avoid ambiguous
        // mappings
        modelMapper.getConfiguration()
                .setMatchingStrategy(org.modelmapper.convention.MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        // Define converter to extract names from Set<Director>
        Converter<Set<Director>, Set<String>> directorConverter = context -> context.getSource() == null ? null
                : context.getSource().stream()
                        .map(Director::getName)
                        .collect(Collectors.toSet());

        // Define converter to extract names from Set<Actor>
        Converter<Set<Actor>, Set<String>> actorConverter = context -> context.getSource() == null ? null
                : context.getSource().stream()
                        .map(Actor::getName)
                        .collect(Collectors.toSet());

        // Configure custom mapping rules when mapping from Movie to MovieDto
        modelMapper.typeMap(Movie.class, MovieDto.class).addMappings(mapper -> {
            mapper.using(directorConverter).map(Movie::getDirectors, MovieDto::setDirectors);
            mapper.using(actorConverter).map(Movie::getActors, MovieDto::setActors);
        });

        // Configure mapping rules when mapping from MovieDto to Movie
        modelMapper.typeMap(MovieDto.class, Movie.class).addMappings(mapper -> {
            // Skip mapping directors and actors as they will be handled separately in
            // service layer
            mapper.skip(Movie::setDirectors);
            mapper.skip(Movie::setActors);
        });

        // Configure ScreeningSchedule to ScreeningScheduleDto mapping to skip related
        // entity fields
        modelMapper.typeMap(group6.cinema_project.entity.ScreeningSchedule.class,
                group6.cinema_project.dto.ScreeningScheduleDto.class).addMappings(mapper -> {
                    // Skip all related entity fields to avoid cascade loading issues
                    mapper.skip(group6.cinema_project.dto.ScreeningScheduleDto::setMovieName);
                    mapper.skip(group6.cinema_project.dto.ScreeningScheduleDto::setMovieImage);
                    mapper.skip(group6.cinema_project.dto.ScreeningScheduleDto::setMovieDuration);
                    mapper.skip(group6.cinema_project.dto.ScreeningScheduleDto::setMovieRating);
                    mapper.skip(group6.cinema_project.dto.ScreeningScheduleDto::setMovieGenre);
                    mapper.skip(group6.cinema_project.dto.ScreeningScheduleDto::setScreeningRoomName);
                    mapper.skip(group6.cinema_project.dto.ScreeningScheduleDto::setScreeningRoomCapacity);
                    mapper.skip(group6.cinema_project.dto.ScreeningScheduleDto::setBranchName);
                    mapper.skip(group6.cinema_project.dto.ScreeningScheduleDto::setBranchAddress);
                });

        // Configure ScreeningRoom to ScreeningRoomDto mapping to skip collections
        modelMapper.typeMap(group6.cinema_project.entity.ScreeningRoom.class,
                group6.cinema_project.dto.ScreeningRoomDto.class).addMappings(mapper -> {
                    // Skip all collection fields to avoid cascade loading issues
                    mapper.skip(group6.cinema_project.dto.ScreeningRoomDto::setSeats);
                    mapper.skip(group6.cinema_project.dto.ScreeningRoomDto::setScreeningSchedules);
                    mapper.skip(group6.cinema_project.dto.ScreeningRoomDto::setBranchName);
                    mapper.skip(group6.cinema_project.dto.ScreeningRoomDto::setBranchAddress);
                });

        // Configure Movie to MovieDto mapping to skip collections
        modelMapper.typeMap(group6.cinema_project.entity.Movie.class,
                group6.cinema_project.dto.MovieDto.class).addMappings(mapper -> {
                    // Skip all collection fields to avoid cascade loading issues
                    mapper.skip(group6.cinema_project.dto.MovieDto::setDirectors);
                    mapper.skip(group6.cinema_project.dto.MovieDto::setActors);
                });

        return modelMapper;
    }
}
