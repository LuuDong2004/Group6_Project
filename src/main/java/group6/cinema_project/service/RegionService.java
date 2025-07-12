package group6.cinema_project.service;

import java.util.List;

import group6.cinema_project.dto.RegionDto;

public interface RegionService {
    List<RegionDto> findAll();
    RegionDto findById(int id);
    RegionDto save(RegionDto regionDto);
    void deleteById(int id);
    
    // Kiểm tra trùng lặp khi thêm mới
    boolean isDuplicateRegion(String name, String type);
    
    // Kiểm tra trùng lặp khi cập nhật (trừ ID hiện tại)
    boolean isDuplicateRegionForUpdate(String name, String type, int id);
}