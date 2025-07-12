package group6.cinema_project.controller;

import group6.cinema_project.service.ScreeningRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import group6.cinema_project.dto.ScreeningRoomDto;
import group6.cinema_project.service.IScreeningRoomService;

@Controller
@RequestMapping("/admin/rooms")
public class AdminScreeningRoomController {
    @Autowired
    private ScreeningRoomService screeningRoomService;

    @GetMapping
    public String listRooms(Model model) {
        model.addAttribute("rooms", screeningRoomService.getAllRooms());
        model.addAttribute("room", new ScreeningRoomDto());
        return "room_management";
    }

    @PostMapping("/add")
    public String addRoom(@ModelAttribute("room") ScreeningRoomDto roomDto, RedirectAttributes redirectAttributes) {
        screeningRoomService.saveOrUpdate(roomDto);
        redirectAttributes.addFlashAttribute("success", "Thêm phòng chiếu thành công!");
        return "redirect:/admin/rooms";
    }

    @GetMapping("/edit/{id}")
    public String editRoomForm(@PathVariable int id, Model model) {
        ScreeningRoomDto room = screeningRoomService.getRoomById(id);
        model.addAttribute("room", room);
        return "admin_room_edit";
    }

    @PostMapping("/edit/{id}")
    public String editRoom(@PathVariable int id, @ModelAttribute("room") ScreeningRoomDto roomDto, RedirectAttributes redirectAttributes) {
        roomDto.setId(id);
        screeningRoomService.saveOrUpdate(roomDto);
        redirectAttributes.addFlashAttribute("success", "Cập nhật phòng chiếu thành công!");
        return "redirect:/admin/rooms";
    }

    @GetMapping("/delete/{id}")
    public String deleteRoom(@PathVariable int id, RedirectAttributes redirectAttributes) {
        screeningRoomService.deleteRoom(id);
        redirectAttributes.addFlashAttribute("success", "Xóa phòng chiếu thành công!");
        return "redirect:/admin/rooms";
    }
} 