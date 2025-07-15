package group6.cinema_project.service.impl;

import group6.cinema_project.dto.BranchDto;
import group6.cinema_project.entity.Branch;
import group6.cinema_project.repository.BranchRepository;
import group6.cinema_project.service.IBranchService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BranchServiceImpl implements IBranchService {

    private final BranchRepository branchRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<BranchDto> getBranchById(Integer id) {
        return branchRepository.findById(id)
                .map(this::convertToBasicDto);
    }

    @Override
    public BranchDto saveOrUpdateBranch(BranchDto branchDto) {
        Branch branch = modelMapper.map(branchDto, Branch.class);
        Branch savedBranch = branchRepository.save(branch);
        return modelMapper.map(savedBranch, BranchDto.class);
    }

    @Override
    public void deleteBranch(Integer id) {
        if (!branchRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot delete. Branch not found with ID: " + id);
        }
        branchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchDto> getAllBranches() {
        return branchRepository.findAll().stream()
                .map(this::convertToBasicDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Branch> getAllBranchEntities() {
        return branchRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchDto> getFilteredBranches(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllBranches();
        }
        return branchRepository.findByNameContainingIgnoreCase(searchTerm.trim()).stream()
                .map(this::convertToBasicDto)
                .collect(Collectors.toList());
    }

    private BranchDto convertToBasicDto(Branch branch) {
        BranchDto dto = new BranchDto();
        dto.setId(branch.getId());
        dto.setName(branch.getName());
        dto.setDescription(branch.getDescription());
        dto.setAddress(branch.getAddress());
        return dto;
    }

    
}
