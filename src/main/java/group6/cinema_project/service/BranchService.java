package group6.cinema_project.service;

import java.util.List;

import org.springframework.data.domain.Page;

import group6.cinema_project.dto.BranchDto;

public interface BranchService {
    List<BranchDto> findAll();
    BranchDto findById(int id);
    BranchDto save(BranchDto branchDto);
    void deleteById(int id);
    List<BranchDto> findByCinemaChainId(int cinemaChainId);
    Page<BranchDto> getBranchesPage(int page, int size);
}