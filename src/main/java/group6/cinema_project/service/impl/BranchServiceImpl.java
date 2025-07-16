package group6.cinema_project.service.impl;

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
import group6.cinema_project.service.BranchService;

@Service
public class BranchServiceImpl implements BranchService {
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
} 