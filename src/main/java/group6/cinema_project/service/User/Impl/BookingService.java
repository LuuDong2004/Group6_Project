package group6.cinema_project.service.User.Impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import group6.cinema_project.dto.BookedFoodDto;
import group6.cinema_project.dto.BookingDto;
import group6.cinema_project.dto.BookingRequest;
import group6.cinema_project.entity.Booking;
import group6.cinema_project.entity.BookingFood;
import group6.cinema_project.entity.Food;
import group6.cinema_project.entity.ScreeningSchedule;
import group6.cinema_project.entity.SeatReservation;
import group6.cinema_project.entity.User;
import group6.cinema_project.repository.User.BookingFoodRepository;
import group6.cinema_project.repository.User.BookingRepository;
import group6.cinema_project.repository.User.FoodRepository;
import group6.cinema_project.repository.User.ScheduleRepository;
import group6.cinema_project.repository.User.SeatReservationRepository;
import group6.cinema_project.repository.User.UserRepository;
import group6.cinema_project.service.User.IBookingService;
import group6.cinema_project.service.User.MailService;



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

    @Autowired
    private SeatReservationRepository seatReservationRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private BookingFoodRepository bookingFoodRepository;
    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private VoucherServiceImpl voucherService;

    @Override
    public List<BookingDto> createBooking(BookingRequest request) {
        try {
            // Lấy user thực tế đang đăng nhập từ SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user với email: " + email));

            // Tìm schedule theo ID
            ScreeningSchedule schedule = scheduleRepository.findById(request.getScheduleId())
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
            booking.setVoucherCode(request.getVoucherCode());


            // Lưu booking
            booking = bookingRepository.save(booking);

            // Đặt các ghế đã chọn
            for (Integer seatId : request.getSeatIds()) {
                seatReservationService.reserveSeat(seatId, request.getScheduleId(), user.getId(), (int) booking.getId());
            }

            // Lưu thông tin food vào bảng BookingFood
            if (request.getFoodItems() != null) {
                for (Map.Entry<String, Integer> entry : request.getFoodItems().entrySet()) {
                    Integer foodId = Integer.valueOf(entry.getKey());
                    Integer quantity = entry.getValue();
                    if (quantity != null && quantity > 0) {
                        Food food = foodRepository.findById(foodId)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy food với id: " + foodId));
                        BookingFood bookingFood = new BookingFood();
                        bookingFood.setBooking(booking);
                        bookingFood.setFood(food);
                        bookingFood.setQuantity(quantity);
                        bookingFood.setPrice(food.getPrice().intValue());
                        bookingFood.setStatus("PENDING");
                        bookingFoodRepository.save(bookingFood);
                    }
                }
            }

            // ĐẢM BẢO: Gán bookingId cho các reservation PENDING vừa giữ chỗ (nếu có)
            List<SeatReservation> pendingReservations = seatReservationRepository.findAll();
            for (SeatReservation reservation : pendingReservations) {
                if (reservation.getStatus().equals("PENDING")
                        && reservation.getSchedule().getId().equals(request.getScheduleId())
                        && reservation.getBooking() == null
                        && reservation.getSeat() != null
                        && request.getSeatIds().contains(reservation.getSeat().getId())) {
                    reservation.setBooking(booking);
                    seatReservationRepository.save(reservation);
                }
            }


            // Chuyển đổi sang DTO và trả về
            BookingDto bookingDto = modelMapper.map(booking, BookingDto.class);
            // Map schedule với mapToDto để set các trường chuỗi thời gian
            if (booking.getSchedule() != null) {
                bookingDto.setSchedule(scheduleService.mapToDto(booking.getSchedule()));
            }
            bookingDto.setVoucherCode(booking.getVoucherCode());
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
            List<Booking> bookings = bookingRepository.findByUserId(userId);
            return bookings.stream()
                    .map(booking -> {
                        BookingDto dto = modelMapper.map(booking, BookingDto.class);
                        if (booking.getSchedule() != null) {
                            dto.setSchedule(scheduleService.mapToDto(booking.getSchedule()));
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách booking: " + e.getMessage());
        }
    }

    @Override
    public List<BookingDto> getPaidBookingsByUserIdAndDateAfter(Integer userId, LocalDate fromDate) {
        try {
            List<Booking> bookings = bookingRepository.findByUserIdAndDateAfterAndStatus(userId, fromDate, "PAID");
            return bookings.stream().map(booking -> {
                BookingDto dto = modelMapper.map(booking, BookingDto.class);
                if (booking.getSchedule() != null) {
                    dto.setSchedule(scheduleService.mapToDto(booking.getSchedule()));
                }
                return dto;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách booking PAID theo thời gian: " + e.getMessage());
        }
    }

    @Override
    public List<BookingDto> getPaidBookingsByUserIdAndDateAfterSortedByShowDateDesc(Integer userId, LocalDate fromDate) {
        try {
            List<Booking> bookings = bookingRepository.findByUserIdAndDateAfterAndStatus(userId, fromDate, "PAID");
            List<BookingDto> dtos = bookings.stream().map(booking -> {
                BookingDto dto = modelMapper.map(booking, BookingDto.class);
                if (booking.getSchedule() != null) {
                    dto.setSchedule(scheduleService.mapToDto(booking.getSchedule()));
                }
                return dto;
            }).collect(Collectors.toList());
            // Sắp xếp theo ngày suất chiếu giảm dần
            dtos.sort((b1, b2) -> {
                if (b1.getSchedule() != null && b2.getSchedule() != null && b1.getSchedule().getScreeningDate() != null && b2.getSchedule().getScreeningDate() != null) {
                    return b2.getSchedule().getScreeningDate().compareTo(b1.getSchedule().getScreeningDate());
                } else if (b1.getDate() != null && b2.getDate() != null) {
                    return b2.getDate().compareTo(b1.getDate());
                } else {
                    return 0;
                }
            });
            return dtos;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách booking PAID theo thời gian (sorted): " + e.getMessage());
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

            // Cập nhật trạng thái BookingFood thành CANCELLED
            List<BookingFood> foods = bookingFoodRepository.findByBookingId(bookingId);
            for (BookingFood food : foods) {
                food.setStatus("CANCELLED");
                bookingFoodRepository.save(food);
            }


            return true;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi hủy booking: " + e.getMessage());
        }
    }

    @Override
    public BookingDto getBookingById(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking với ID: " + bookingId));
        BookingDto bookingDto = modelMapper.map(booking, BookingDto.class);
        // Map schedule với mapToDto để set các trường chuỗi thời gian
        if (booking.getSchedule() != null) {
            bookingDto.setSchedule(scheduleService.mapToDto(booking.getSchedule()));
        }
        // Map danh sách ghế
        List<SeatReservation> reservations = seatReservationRepository.findByBookingId(bookingId);
        List<String> seatNames = reservations.stream()
            .map(r -> r.getSeat().getName())
            .collect(Collectors.toList());
        bookingDto.setSeatNames(seatNames);
        // Map danh sách food đã đặt
        List<BookedFoodDto> foodList = bookingFoodRepository.findByBookingId(bookingId)
            .stream()
            .map(bf -> {
                BookedFoodDto dto = new BookedFoodDto();
                dto.setName(bf.getFood().getName());
                dto.setImage(bf.getFood().getImage());
                dto.setQuantity(bf.getQuantity());
                dto.setPrice(bf.getPrice());
                return dto;
            })
            .collect(Collectors.toList());
        bookingDto.setFoodList(foodList);
        return bookingDto;
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
    
    @Override
    public void confirmBookingPaid(int bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy booking"));
        
        // Cập nhật trạng thái booking
        booking.setStatus("PAID");
        bookingRepository.save(booking);
        
        // Cập nhật trạng thái các SeatReservation liên quan
        List<SeatReservation> reservations = seatReservationRepository.findByBookingId(bookingId);
        for (SeatReservation reservation : reservations) {
            reservation.setStatus("RESERVED");
            seatReservationRepository.save(reservation);
        }

        // Cập nhật trạng thái BookingFood thành PAID
        List<BookingFood> foods = bookingFoodRepository.findByBookingId(bookingId);
        for (BookingFood food : foods) {
            food.setStatus("PAID");
            bookingFoodRepository.save(food);
        }



        // Gửi email vé điện tử sau khi thanh toán thành công
        try {
            BookingDto bookingDto = modelMapper.map(booking, BookingDto.class);
            // Map schedule với mapToDto để set các trường chuỗi thời gian
            if (booking.getSchedule() != null) {
                bookingDto.setSchedule(scheduleService.mapToDto(booking.getSchedule()));
            }
            // Map danh sách ghế
            List<String> seatNames = reservations.stream()
                .map(r -> r.getSeat().getName())
                .collect(Collectors.toList());
            bookingDto.setSeatNames(seatNames);
            
            // Gửi email vé điện tử
            mailService.sendETicketEmail(bookingDto, booking.getUser().getEmail());
            System.out.println("Đã gửi email vé điện tử cho booking ID: " + bookingId);
        } catch (Exception e) {
            System.err.println("Lỗi gửi email vé điện tử: " + e.getMessage());
            // Không throw exception để không ảnh hưởng đến việc xác nhận thanh toán
        }
    }

    @Override
    public void cancelPendingBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking != null && "PENDING".equals(booking.getStatus())) {
            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);
            // Hủy ghế PENDING
            List<SeatReservation> reservations = seatReservationRepository.findByBookingId(bookingId);
            for (SeatReservation r : reservations) {
                if ("PENDING".equals(r.getStatus())) seatReservationRepository.delete(r);
            }
            // Hủy food PENDING
            List<BookingFood> foods = bookingFoodRepository.findByBookingId(bookingId);
            for (BookingFood f : foods) {
                if ("PENDING".equals(f.getStatus())) bookingFoodRepository.delete(f);
            }
        }
    }

    @Override
    public void updateBookingAmount(Integer bookingId, double newAmount) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        booking.setAmount(newAmount);
        bookingRepository.save(booking);
    }

    // Admin methods
    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookings() {
        return bookingRepository.findAllOrderByDateDesc().stream()
                .map(this::convertToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByScheduleId(Integer scheduleId) {
        return bookingRepository.findByScheduleIdOrderByDateDesc(scheduleId).stream()
                .map(this::convertToBookingDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert Booking entity to BookingDto with all related information
     */
    private BookingDto convertToBookingDto(Booking booking) {
        BookingDto bookingDto = modelMapper.map(booking, BookingDto.class);
        
        // Map schedule với mapToDto để set các trường chuỗi thời gian
        if (booking.getSchedule() != null) {
            bookingDto.setSchedule(scheduleService.mapToDto(booking.getSchedule()));
        }
        
        // Map danh sách ghế
        List<SeatReservation> reservations = seatReservationRepository.findByBookingId(booking.getId());
        List<String> seatNames = reservations.stream()
            .map(r -> r.getSeat().getName())
            .collect(Collectors.toList());
        bookingDto.setSeatNames(seatNames);
        
        // Map danh sách food đã đặt
        List<BookedFoodDto> foodList = bookingFoodRepository.findByBookingId(booking.getId())
            .stream()
            .map(bf -> {
                BookedFoodDto dto = new BookedFoodDto();
                dto.setName(bf.getFood().getName());
                dto.setImage(bf.getFood().getImage());
                dto.setQuantity(bf.getQuantity());
                dto.setPrice(bf.getPrice());
                return dto;
            })
            .collect(Collectors.toList());
        bookingDto.setFoodList(foodList);
        
        return bookingDto;
    }

    // Pagination method for admin
    @Override
    @Transactional(readOnly = true)
    public Page<BookingDto> getBookingsPage(int page, int size, String searchTerm, String status, Integer scheduleId) {
        // Tạo pageable object
        Pageable pageable = PageRequest.of(page, size);
        
        // Lấy tất cả bookings theo điều kiện
        List<BookingDto> allBookings;
        
        if (scheduleId != null) {
            // Lấy booking theo suất chiếu cụ thể
            allBookings = getBookingsByScheduleId(scheduleId);
        } else {
            // Lấy tất cả booking
            allBookings = getAllBookings();
        }
        
        // Filter theo search term nếu có
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            allBookings = allBookings.stream()
                .filter(booking -> 
                    booking.getUser().getEmail().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    booking.getCode().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    (booking.getSchedule() != null && booking.getSchedule().getMovie() != null && 
                     booking.getSchedule().getMovie().getName().toLowerCase().contains(searchTerm.toLowerCase()))
                )
                .collect(Collectors.toList());
        }
        
        // Filter theo status nếu có
        if (status != null && !status.trim().isEmpty() && !"all".equals(status)) {
            allBookings = allBookings.stream()
                .filter(booking -> status.equals(booking.getStatus()))
                .collect(Collectors.toList());
        }
        
        // Thực hiện phân trang thủ công
        int totalElements = allBookings.size();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), totalElements);
        
        List<BookingDto> pageContent = allBookings.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, totalElements);
    }
}