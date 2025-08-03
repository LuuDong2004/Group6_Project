package group6.cinema_project.entity;

<<<<<<< Updated upstream
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;
=======
import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Time;

import java.util.Date;
>>>>>>> Stashed changes

@Entity
public class ScreeningSchedule {
    @Id
    private Long id;
    private Long screeningRoomId;
    private Long branchId;
    private String screeningDate;
    private String startTime;
    private String endTime;

    @ManyToOne
<<<<<<< Updated upstream
    private Movie movie;

    @OneToMany(mappedBy = "screeningSchedule")
    private List<Booking> bookings;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getScreeningRoomId() { return screeningRoomId; }
    public void setScreeningRoomId(Long screeningRoomId) { this.screeningRoomId = screeningRoomId; }
    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }
    public String getScreeningDate() { return screeningDate; }
    public void setScreeningDate(String screeningDate) { this.screeningDate = screeningDate; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }
    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
}
=======
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "movie_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "screening_room_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ScreeningRoom screeningRoom;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Branch branch;

    @Column(name = "screening_date")
    private Date screeningDate;

    @Column(name = "start_time")
    private Time startTime;

    @Column(name = "end_time")
    private Time endTime;

    @Column(name = "status", nullable = false, length = 255)
    private String status;

}
>>>>>>> Stashed changes
