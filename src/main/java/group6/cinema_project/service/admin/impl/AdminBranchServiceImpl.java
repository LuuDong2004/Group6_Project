package group6.cinema_project.service.admin.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import group6.cinema_project.dto.BranchDto;
import group6.cinema_project.entity.Branch;
import group6.cinema_project.repository.BranchRepository;
import group6.cinema_project.repository.CinemaChainRepository;
import group6.cinema_project.service.admin.AdminBranchService;


@Service
public class AdminBranchServiceImpl implements AdminBranchService {
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private CinemaChainRepository cinemaChainRepository;

    @Override
    public List<BranchDto> findAll() {
        return branchRepository.findAll().stream()
                .map(BranchDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public BranchDto findById(int id) {
        Optional<Branch> branch = branchRepository.findById(id);
        return branch.map(BranchDto::fromEntity).orElse(null);
    }

    @Override
    public BranchDto save(BranchDto branchDto) {
        Branch branch = new Branch();
        branch.setId(branchDto.getId());
        branch.setName(branchDto.getName());
        branch.setDescription(branchDto.getDescription());
        branch.setAddress(branchDto.getAddress());

        // Lấy entity từ repository
        branch.setCinemaChain(cinemaChainRepository.findById(branchDto.getCinemaChainId()).orElse(null));

        Branch saved = branchRepository.save(branch);
        return BranchDto.fromEntity(saved);
    }

    @Override
    public void deleteById(int id) {
        branchRepository.deleteById(id);
    }

    @Override
    public List<BranchDto> findByCinemaChainId(int cinemaChainId) {
        return branchRepository.findByCinemaChainId(cinemaChainId).stream()
                .map(BranchDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BranchDto> getBranchesPage(int page, int size) {
        Page<Branch> branchPage = branchRepository.findAll(PageRequest.of(page, size));
        List<BranchDto> branchDtos = branchPage.getContent().stream().map(BranchDto::fromEntity).collect(Collectors.toList());
        return new PageImpl<>(branchDtos, branchPage.getPageable(), branchPage.getTotalElements());
    }

    @Override
    public Page<Branch> getBranchesPage(int page, int size, String name, String address, String cinemaChain) {
        boolean hasSearch = (name != null && !name.trim().isEmpty()) || (address != null && !address.trim().isEmpty()) || (cinemaChain != null && !cinemaChain.trim().isEmpty());
        if (hasSearch) {
            return branchRepository.searchBranches(
                (name == null || name.isBlank()) ? null : name,
                (address == null || address.isBlank()) ? null : address,
                (cinemaChain == null || cinemaChain.isBlank()) ? null : cinemaChain,
                PageRequest.of(page, size)
            );
        } else {
            return branchRepository.findAll(PageRequest.of(page, size));
        }
    }

    @Override
public boolean isNameDuplicate(String name, Integer id) {
    List<Branch> branches = branchRepository.findByName(name);
    if (id == null) {
        return !branches.isEmpty();
    } else {
        return branches.stream().anyMatch(b -> b.getId() != id);
    }
}
} 