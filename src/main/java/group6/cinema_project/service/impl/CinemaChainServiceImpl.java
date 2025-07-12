package group6.cinema_project.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group6.cinema_project.entity.CinemaChain;
import group6.cinema_project.repository.CinemaChainRepository;
import group6.cinema_project.service.CinemaChainService;

@Service
public class CinemaChainServiceImpl implements CinemaChainService {
    @Autowired
    private CinemaChainRepository cinemaChainRepository;

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
} 