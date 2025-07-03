package group6.cinema_project.repository;

import group6.cinema_project.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PaymentRepository extends JpaRepository<Booking, Long> {


    @Query("SELECT b FROM Booking b WHERE b.code = :bookingCode")
    Booking findByBookingCode(@Param("bookingCode") String bookingCode);


    @Modifying
    @Transactional
    @Query("UPDATE Booking b SET b.status = 'FAILED' WHERE b.code = :transactionCode")
    void setPaymentFailed(@Param("transactionCode") String transactionCode);

    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.user " +
           "LEFT JOIN FETCH b.schedule s " +
           "LEFT JOIN FETCH s.movie " +
           "LEFT JOIN FETCH s.branch " +
           "LEFT JOIN FETCH s.screeningRoom " +
           "WHERE b.id = :bookingId")
    Booking findBookingWithDetailsById(@Param("bookingId") Long bookingId);
}
