package group6.cinema_project.service.Admin;

import group6.cinema_project.dto.BranchDto;
import group6.cinema_project.entity.Branch;

import java.util.List;
import java.util.Optional;

public interface IAdminBranchService {
    Optional<BranchDto> getBranchById(Integer id);

    BranchDto saveOrUpdateBranch(BranchDto branchDto);

    void deleteBranch(Integer id);

    List<BranchDto> getAllBranches();

    List<Branch> getAllBranchEntities();

    List<BranchDto> getFilteredBranches(String searchTerm);
}
