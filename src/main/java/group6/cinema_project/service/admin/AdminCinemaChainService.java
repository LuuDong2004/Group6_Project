package group6.cinema_project.service.admin;

import java.util.List;

import org.springframework.data.domain.Page;

import group6.cinema_project.entity.CinemaChain;

public interface AdminCinemaChainService {
    List<CinemaChain> findAll();
    CinemaChain findById(int id);
    CinemaChain save(CinemaChain cinemaChain);
    void deleteById(int id);
    Page<CinemaChain> getCinemaChainsPage(int page, int size, String search);
    boolean isNameDuplicate(String name, Integer id);
}