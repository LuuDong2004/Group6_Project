package group6.cinema_project.service.Admin.impl;


import java.util.List;
import java.util.Optional;

import group6.cinema_project.repository.Admin.AdminCinemaChainRepository;
import group6.cinema_project.service.Admin.IAdminCinemaChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import group6.cinema_project.entity.CinemaChain;


@Service
public class AdminCinemaChainServiceImpl implements IAdminCinemaChainService {
    @Autowired
    private AdminCinemaChainRepository cinemaChainRepository;

    @Override
    public List<CinemaChain> findAll() {
        return cinemaChainRepository.findAll();
    }

    @Override
    public CinemaChain findById(int id) {
        Optional<CinemaChain> chain = cinemaChainRepository.findById(id);
        return chain.orElse(null);
    }

    @Override
    public CinemaChain save(CinemaChain cinemaChain) {
        return cinemaChainRepository.save(cinemaChain);
    }

    @Override
    public void deleteById(int id) {
        cinemaChainRepository.deleteById(id);
    }

    @Override
    public Page<CinemaChain> getCinemaChainsPage(int page, int size, String search) {
        if (search != null && !search.trim().isEmpty()) {
            return cinemaChainRepository.searchByName(search, PageRequest.of(page, size));
        } else {
            return cinemaChainRepository.findAll(PageRequest.of(page, size));
        }
    }
    @Override
    public boolean isNameDuplicate(String name, Integer id) {
        List<CinemaChain> chains = cinemaChainRepository.findByName(name);
        if (id == null) {
            return !chains.isEmpty();
        } else {
            return chains.stream().anyMatch(c -> c.getId() != id);
        }
    }

}