package group6.cinema_project.service.admin;

import java.util.List;

import org.springframework.data.domain.Page;

import group6.cinema_project.dto.BranchDto;

public interface AdminBranchService {
    List<BranchDto> findAll();
    BranchDto findById(int id);
    BranchDto save(BranchDto branchDto);
    void deleteById(int id);
    List<BranchDto> findByCinemaChainId(int cinemaChainId);
    Page<BranchDto> getBranchesPage(int page, int size);
    boolean isNameDuplicate(String name, Integer id);
    Page<group6.cinema_project.entity.Branch> getBranchesPage(int page, int size, String name, String address, String cinemaChain);
} 