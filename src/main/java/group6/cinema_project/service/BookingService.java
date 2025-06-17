package group6.cinema_project.service;

import group6.cinema_project.dto.BookingDto;
import group6.cinema_project.dto.BookingRequest;
import group6.cinema_project.entity.Booking;
import group6.cinema_project.entity.User;
import group6.cinema_project.entity.Schedule;
import group6.cinema_project.repository.BookingRepository;
import group6.cinema_project.repository.UserRepository;
import group6.cinema_project.repository.ScheduleRepository;
import jakarta.persistence.Id;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingService implements IBookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SeatReservationService seatReservationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<BookingDto> createBooking(BookingRequest request) {
        try {
            // Tìm user theo ID (mặc định là user 1)
            User user = userRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user mặc định với ID: 1"));

            // Tìm schedule theo ID
            Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch chiếu với ID: " + request.getScheduleId()));

            // Tạo mã booking ngẫu nhiên
            String bookingCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            // Tạo booking mới
            Booking booking = new Booking();
            booking.setUser(user);
            booking.setSchedule(schedule);
            booking.setCode(bookingCode);
            booking.setAmount(request.getTotalAmount());
            booking.setStatus("PENDING");
            booking.setDate(LocalDate.now());
            booking.setNotes(request.getNotes());

            // Lưu booking
            booking = bookingRepository.save(booking);

            // Đặt các ghế đã chọn
            for (Integer seatId : request.getSeatIds()) {
                seatReservationService.reserveSeat(seatId, request.getScheduleId(), 1); // Sử dụng userId = 1
            }

            // Chuyển đổi sang DTO và trả về
            BookingDto bookingDto = modelMapper.map(booking, BookingDto.class);
            List<BookingDto> result = new ArrayList<>();
            result.add(bookingDto);
            return result;

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo booking: " + e.getMessage());
        }
    }

    @Override
    public List<BookingDto> getBookingsByUserId(Integer userId) {
        try {
            List<Booking> bookings = bookingRepository.findByUserId(1); // Sử dụng userId = 1
            return bookings.stream()
                    .map(booking -> modelMapper.map(booking, BookingDto.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách booking: " + e.getMessage());
        }
    }

    @Override
    public boolean cancelBooking(Integer bookingId) {
        try {
            // Tìm booking theo ID
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy booking với ID: " + bookingId));

            // Kiểm tra trạng thái booking
            if (!"PENDING".equals(booking.getStatus())) {
                throw new RuntimeException("Không thể hủy booking với trạng thái: " + booking.getStatus());
            }

            // Cập nhật trạng thái booking thành CANCELLED
            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);

            return true;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi hủy booking: " + e.getMessage());
        }
    }

    @Override
    public BookingDto getBookingById(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking với ID: " + bookingId));
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    public boolean updateBookingStatus(Integer bookingId, String status) {
        try {
            // Tìm booking theo ID
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy booking với ID: " + bookingId));

            // Cập nhật trạng thái booking
            booking.setStatus(status);
            bookingRepository.save(booking);

            return true;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật trạng thái booking: " + e.getMessage());
        }
    }
}