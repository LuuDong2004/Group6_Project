package group6.cinema_project.service.Admin;

import group6.cinema_project.entity.CinemaChain;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ICinemaChainService {
    List<CinemaChain> findAll();
    CinemaChain findById(int id);
    CinemaChain save(CinemaChain cinemaChain);
    void deleteById(int id);
    Page<CinemaChain> getCinemaChainsPage(int page, int size);
}