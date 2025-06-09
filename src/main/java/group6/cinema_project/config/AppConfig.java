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

        // Định nghĩa bộ chuyển đổi để lấy danh sách tên từ Set<Director>
        Converter<Set<Director>, Set<String>> directorConverter = context -> context.getSource() == null ? null
                : context.getSource().stream()
                        .map(Director::getName) // Lấy tên từ mỗi đối tượng Director
                        .collect(Collectors.toSet());

        // Định nghĩa bộ chuyển đổi để lấy danh sách tên từ Set<Actor>
        Converter<Set<Actor>, Set<String>> actorConverter = context -> context.getSource() == null ? null
                : context.getSource().stream()
                        .map(Actor::getName) // Lấy tên từ mỗi đối tượng Actor
                        .collect(Collectors.toSet());

        // Áp dụng các quy tắc chuyển đổi tùy chỉnh khi map từ Movie sang MovieDto
        modelMapper.typeMap(Movie.class, MovieDto.class).addMappings(mapper -> {
            // "Dạy" ModelMapper cách sử dụng converter đã định nghĩa ở trên
            mapper.using(directorConverter).map(Movie::getDirectors, MovieDto::setDirectors);
            mapper.using(actorConverter).map(Movie::getActors, MovieDto::setActors);
        });

        // Áp dụng quy tắc chuyển đổi khi map từ MovieDto sang Movie
        modelMapper.typeMap(MovieDto.class, Movie.class).addMappings(mapper -> {
            // Bỏ qua việc map directors và actors vì chúng sẽ được xử lý riêng
            mapper.skip(Movie::setDirectors);
            mapper.skip(Movie::setActors);
        });

        return modelMapper;
    }
}
