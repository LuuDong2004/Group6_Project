package group6.cinema_project.service;

import group6.cinema_project.dto.SeatDto;
import group6.cinema_project.entity.ScreeningRoom;
import group6.cinema_project.entity.Seat;
import group6.cinema_project.repository.SeatRepository;
import group6.cinema_project.repository.TicketRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<SeatDto> getSeatsByScheduleId(Integer scheduleId) {
        //Lấy ra các chỗ ngồi của phòng trong lịch đó
//        ScreeningRoom room = scheduleRepository.getById(scheduleId).getRoom();
//        List<Seat> listSeat = seatRepository.findSeatsByScreeningRoomId((room.getId());
//
//        // Lấy ra các vé đã được đặt trong lịch đó rồi map sang các chỗ ngồi
//        List<Seat> occupiedSeats = ticketRepository.findTicketsBySchedule_Id(scheduleId)
//                .stream().map(ticket -> ticket.getSeat())
//                .collect(Collectors.toList());
//
//        // Map list chỗ ngồi của phòng ở bước 1 sang list dto
//        List<SeatDTO> filteredSeats = listSeat.stream().map(seat -> {
//           SeatDTO seatDTO = modelMapper.map(seat,SeatDTO.class);
//           if(occupiedSeats.stream()
//                   .map(occupiedSeat->occupiedSeat.getId())
//                   .collect(Collectors.toList()).contains(seat.getId())){
//               seatDTO.setIsOccupied(1); // Nếu ghế nào nằm trong list ghế đã được occupied thì set = 1
//           }
//           return seatDTO;
//        }).collect(Collectors.toList());
//
//        return  filteredSeats;

        return null;
    }

}
