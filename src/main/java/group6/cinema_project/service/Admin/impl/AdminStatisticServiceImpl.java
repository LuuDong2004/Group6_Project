package group6.cinema_project.service.Admin.impl;

import group6.cinema_project.entity.Booking;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.entity.ScreeningSchedule;
import group6.cinema_project.entity.User;
import group6.cinema_project.repository.User.BookingRepository;
import group6.cinema_project.repository.User.MovieRepository;
import group6.cinema_project.repository.User.ScreeningScheduleRepository;
import group6.cinema_project.repository.User.UserRepository;
import group6.cinema_project.repository.User.TicketRepository;
import group6.cinema_project.service.Admin.IAdminStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class AdminStatisticServiceImpl implements IAdminStatisticService {

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
                .filter(booking -> booking.getSchedule() != null)
                .collect(Collectors.groupingBy(
                        booking -> booking.getSchedule().getMovie().getName(),
                        Collectors.summingDouble(Booking::getAmount)))
                .entrySet().stream()
                .map(entry -> new Object[] { entry.getKey(), entry.getValue() })
                .collect(Collectors.toList());
    }

    public Map<Integer, Long> getTicketCountByScreeningSchedule() {
        return ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getSchedule() != null)
                .collect(Collectors.groupingBy(
                        ticket -> ticket.getSchedule().getId(),
                        Collectors.counting()));
    }

    public Map<String, Object> getUserMovieStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // User statistics
        long totalUsers = userRepository.count();
        statistics.put("totalUsers", totalUsers);

        // Movie statistics
        long totalMovies = movieRepository.count();
        statistics.put("totalMovies", totalMovies);

        // Booking statistics
        long totalBookings = bookingRepository.count();
        statistics.put("totalBookings", totalBookings);

        // Revenue statistics
        double totalRevenue = bookingRepository.findAll().stream()
                .mapToDouble(Booking::getAmount)
                .sum();
        statistics.put("totalRevenue", totalRevenue);

        // Screening schedule statistics
        long totalSchedules = screeningScheduleRepository.count();
        statistics.put("totalSchedules", totalSchedules);

        // Ticket statistics
        long totalTickets = ticketRepository.count();
        statistics.put("totalTickets", totalTickets);

        // Top movies by revenue
        List<Object[]> topMoviesByRevenue = getMovieRevenueStats();
        statistics.put("topMoviesByRevenue", topMoviesByRevenue);

        return statistics;
    }

    @Override
    public List<Map<String, Object>> getUserMovieStatisticsList() {
        List<Map<String, Object>> statisticsList = new ArrayList<>();

        // Lấy thống kê từ booking - nhóm theo user và movie
        Map<String, Map<String, Object>> userMovieStats = new HashMap<>();

        List<Booking> bookings = bookingRepository.findAll();
        for (Booking booking : bookings) {
            if (booking.getUser() != null && booking.getSchedule() != null
                    && booking.getSchedule().getMovie() != null) {
                String userName = booking.getUser().getUserName();
                String movieName = booking.getSchedule().getMovie().getName();
                String key = userName + "_" + movieName;

                userMovieStats.computeIfAbsent(key, k -> {
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("userName", userName);
                    stat.put("movieName", movieName);
                    stat.put("totalTickets", 0);
                    stat.put("totalAmount", 0.0);
                    return stat;
                });

                Map<String, Object> stat = userMovieStats.get(key);
                stat.put("totalTickets", (Integer) stat.get("totalTickets") + 1);
                stat.put("totalAmount", (Double) stat.get("totalAmount") + booking.getAmount());
            }
        }

        statisticsList.addAll(userMovieStats.values());
        return statisticsList;
    }

    @Override
    public List<String> getMovieRevenueLabels() {
        return getMovieRevenueStats().stream()
                .map(stat -> (String) stat[0])
                .collect(Collectors.toList());
    }

    @Override
    public List<Double> getMovieRevenueData() {
        return getMovieRevenueStats().stream()
                .map(stat -> (Double) stat[1])
                .collect(Collectors.toList());
    }
}