package group6.cinema_project.controller.User;

import group6.cinema_project.dto.TicketDto;
import group6.cinema_project.service.User.ITicketService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("api/tickets")
public class TicketController {
    @Autowired
    private ITicketService ticketService;

    @GetMapping("get/{id}")
    public ResponseEntity<List<TicketDto>> getTicketById(@PathVariable(name = "id") Integer CustomerId) {
        List<TicketDto> tickets = ticketService.getTicketsByCustomerId(CustomerId);

        return ResponseEntity.ok(tickets);
    }


}
