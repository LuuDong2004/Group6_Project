package group6.cinema_project.service.User;

import group6.cinema_project.dto.TicketDto;


import java.util.List;

public interface ITicketService {
    List<TicketDto> getTicketsByCustomerId(Integer userId);
//    List<TicketDto> getTicketByMovieId(Integer movieId);
}
