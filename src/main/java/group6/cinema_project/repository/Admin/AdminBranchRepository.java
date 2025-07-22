package group6.cinema_project.repository.Admin;

import group6.cinema_project.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminBranchRepository extends JpaRepository<Branch, Integer> {

     /**
      * Tìm chi nhánh theo tên (không phân biệt hoa thường)
      * 
      * @param name Tên chi nhánh cần tìm
      * @return Danh sách chi nhánh có tên chứa từ khóa tìm kiếm
      */
     List<Branch> findByNameContainingIgnoreCase(String name);
}
