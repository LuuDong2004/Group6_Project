package group6.cinema_project.service.impl;

import group6.cinema_project.entity.Booking;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.entity.ScreeningSchedule;
import group6.cinema_project.entity.User;
import group6.cinema_project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticServiceImpl {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ScreeningScheduleRepository screeningScheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public List<ScreeningSchedule> getAllScreeningSchedules() {
        return screeningScheduleRepository.findAll();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Object[]> getMovieRevenueStats() {
        return bookingRepository.findAll().stream()
                .filter(booking -> booking.getScreeningSchedule() != null)
                .collect(Collectors.groupingBy(
                        booking -> booking.getScreeningSchedule().getMovie().getName(),
                        Collectors.summingDouble(Booking::getTotalAmount)))
                .entrySet().stream()
                .map(entry -> new Object[]{entry.getKey(), entry.getValue()})
                .collect(Collectors.toList());
    }

    public Map<Long, Long> getTicketCountByScreeningSchedule() {
        return ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getScreeningSchedule() != null)
                .collect(Collectors.groupingBy(
                        ticket -> ticket.getScreeningSchedule().getId(),
                        Collectors.counting()
                ));
    }

}