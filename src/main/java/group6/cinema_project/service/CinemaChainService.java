package group6.cinema_project.service;

import java.util.List;

import group6.cinema_project.entity.CinemaChain;

public interface CinemaChainService {
    List<CinemaChain> findAll();
    CinemaChain findById(int id);
    CinemaChain save(CinemaChain cinemaChain);
    void deleteById(int id);
} 