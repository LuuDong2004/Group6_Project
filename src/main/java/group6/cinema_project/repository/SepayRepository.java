package group6.cinema_project.repository;

import group6.cinema_project.entity.TransactionSepay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface SepayRepository extends JpaRepository<TransactionSepay, Long> {
    TransactionSepay findByTransactionId(String Id);
}
