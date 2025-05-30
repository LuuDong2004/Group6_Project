package group6.cinema_project.service;

import group6.cinema_project.dto.SeatDto;
import group6.cinema_project.entity.ScreeningRoom;
import group6.cinema_project.entity.Seat;
import group6.cinema_project.repository.ScheduleRepository;
import group6.cinema_project.repository.SeatRepository;
import group6.cinema_project.repository.TicketRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SeatService implements ISeatService {
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ITicketService ticketService;

    @Autowired
    private ScheduleRepository scheduleRepository;


    public List<SeatDto> getSeatsByScheduleId(Integer scheduleId) {
//        //Lấy ra các chỗ ngồi của phòng trong lịch đó
//        ScreeningRoom room = scheduleRepository.findById(scheduleId).getScreeningRoom();
//        List<Seat> listSeat = seatRepository.findSeatsByScreeningRoomId(room.getId());
//
//        // Lấy ra các vé đã được đặt trong lịch đó rồi map sang các chỗ ngồi
//        List<Seat> occupiedSeats = ticketRepository.findTicketsByScreeningScheduleId(scheduleId)
//                .stream().map(ticket -> ticket.getSeat())
//                .collect(Collectors.toList());
//
//        // Map list chỗ ngồi của phòng ở bước 1 sang list dto
//        List<SeatDto> filteredSeats = listSeat.stream().map(seat -> {
//            SeatDto seatDto = modelMapper.map(seat, SeatDto.class);
//            if (occupiedSeats.stream()
//                    .map(occupiedSeat -> occupiedSeat.getId())
//                    .collect(Collectors.toList()).contains(seat.getId())) {
//                seatDto.setIsOccupied(1); // Nếu ghế nào nằm trong list ghế đã được occupied thì set = 1
//            }
//            return seatDto;
//        }).collect(Collectors.toList());
//        return filteredSeats;
        // Lấy lịch chiếu với kiểm tra null phù hợp
        var schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch chiếu với id: " + scheduleId));

        ScreeningRoom room = schedule.getScreeningRoom();
        List<Seat> listSeat = seatRepository.findSeatsByScreeningRoomId(room.getId());

        // Lấy danh sách ID ghế đã được đặt
        Set<Integer> occupiedSeatIds = ticketRepository.findTicketsByScreeningScheduleId(scheduleId)
                .stream()
                .map(ticket -> ticket.getSeat().getId())
                .collect(Collectors.toSet());

        // Chuyển đổi sang DTO với trạng thái ghế đã được đặt
        return listSeat.stream()
                .map(seat -> {
                    SeatDto seatDto = modelMapper.map(seat, SeatDto.class);
                    seatDto.setIsOccupied(occupiedSeatIds.contains(seat.getId()) ? 1 : 0);
                    return seatDto;
                })
                .collect(Collectors.toList());
    }
}
