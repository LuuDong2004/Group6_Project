package group6.cinema_project.repository.User;

import group6.cinema_project.entity.SeatReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatReservationRepository extends JpaRepository<SeatReservation, Integer> {
    
    @Query("SELECT sr FROM SeatReservation sr " +
           "WHERE sr.schedule.id = :scheduleId " +
           "AND (sr.status = 'RESERVED' OR sr.status = 'PENDING')")
    List<SeatReservation> findActiveReservationsByScheduleId(@Param("scheduleId") Integer scheduleId);

    @Query("SELECT COUNT(sr) > 0 FROM SeatReservation sr " +
           "WHERE sr.seat.id = :seatId " +
           "AND sr.schedule.id = :scheduleId " +
           "AND sr.status = 'RESERVED'")
    boolean isSeatReserved(@Param("seatId") Integer seatId, 
                          @Param("scheduleId") Integer scheduleId);

    @Query("SELECT sr FROM SeatReservation sr " +
           "WHERE sr.seat.id = :seatId " +
           "AND sr.schedule.id = :scheduleId " +
           "AND sr.status = 'RESERVED'")
    Optional<SeatReservation> findBySeatIdAndScheduleId(@Param("seatId") Integer seatId,
                                                      @Param("scheduleId") Integer scheduleId);


    @Query("SELECT COUNT(sr) > 0 FROM SeatReservation sr WHERE sr.seat.id = :seatId AND sr.schedule.id = :scheduleId AND sr.status = 'PENDING'")
    boolean isSeatPending(@Param("seatId") Integer seatId, @Param("scheduleId") Integer scheduleId);

    @Query("SELECT sr FROM SeatReservation sr " +
           "WHERE sr.status = :status " +
           "AND sr.createDate < :date")
    List<SeatReservation> findByStatusAndCreateDateBefore(@Param("status") String status,
                                                         @Param("date") Date date);



    @Query("SELECT sr FROM SeatReservation sr WHERE sr.booking.id = :bookingId")
    List<SeatReservation> findByBookingId(@Param("bookingId") Integer bookingId);
}
