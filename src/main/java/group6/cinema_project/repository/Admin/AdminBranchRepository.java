package group6.cinema_project.repository.Admin;

import group6.cinema_project.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AdminBranchRepository extends JpaRepository<Branch,Integer> {
     List<Branch> findByNameContainingIgnoreCase(String name);
}
