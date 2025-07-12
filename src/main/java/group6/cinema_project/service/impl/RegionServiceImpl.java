package group6.cinema_project.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group6.cinema_project.dto.RegionDto;
import group6.cinema_project.entity.Region;
import group6.cinema_project.repository.RegionRepository;
import group6.cinema_project.service.RegionService;

@Service
public class RegionServiceImpl implements RegionService {
    @Autowired
    private RegionRepository regionRepository;

    @Override
    public List<RegionDto> findAll() {
        return regionRepository.findAll().stream()
                .map(RegionDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public RegionDto findById(int id) {
        Optional<Region> region = regionRepository.findById(id);
        return region.map(RegionDto::fromEntity).orElse(null);
    }

    @Override
    public RegionDto save(RegionDto regionDto) {
        Region region = regionDto.toEntity();
        Region saved = regionRepository.save(region);
        return RegionDto.fromEntity(saved);
    }

    @Override
    public void deleteById(int id) {
        regionRepository.deleteById(id);
    }
    
    @Override
    public boolean isDuplicateRegion(String name, String type) {
        return regionRepository.findByNameAndType(name, type).isPresent();
    }
    
    @Override
    public boolean isDuplicateRegionForUpdate(String name, String type, int id) {
        return regionRepository.findByNameAndTypeExcludingId(name, type, id).isPresent();
    }
} 