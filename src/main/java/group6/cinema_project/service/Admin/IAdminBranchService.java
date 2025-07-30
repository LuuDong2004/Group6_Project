package group6.cinema_project.service.Admin;

import group6.cinema_project.dto.BranchDto;
import group6.cinema_project.entity.Branch;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IAdminBranchService {
    List<BranchDto> findAll();

    BranchDto findById(int id);

    BranchDto save(BranchDto branchDto);

    void deleteById(int id);

    List<BranchDto> findByCinemaChainId(int cinemaChainId);

    Page<BranchDto> getBranchesPage(int page, int size);

    boolean isNameDuplicate(String name, Integer id);

    Page<Branch> getBranchesPage(int page, int size, String name, String address, String cinemaChain);

    List<BranchDto> getAllBranches();

}
