package group6.cinema_project.service;

import java.util.List;

import group6.cinema_project.entity.Movie;

public interface MovieService {
  List<Movie> findAll();

  Movie findById(Long id);

  Movie saveOrUpdate(Movie movie);

  void delete(Movie movie);

  
} 
