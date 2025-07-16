package group6.cinema_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import group6.cinema_project.dto.BranchDto;
import group6.cinema_project.dto.ScreeningRoomDto;
import group6.cinema_project.service.BranchService;
import group6.cinema_project.service.ScreeningRoomService;

@Controller
@RequestMapping("/admin/screening-rooms")
public class AdminScreeningRoomController {
    
    @Autowired
    private ScreeningRoomService screeningRoomService;
    
    @Autowired
    private BranchService branchService;
    
    @GetMapping("/branch/{branchId}")
    public String listScreeningRooms(@PathVariable int branchId, Model model, RedirectAttributes redirectAttributes,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "size", defaultValue = "5") int size) {
        BranchDto branch = branchService.findById(branchId);
        if (branch == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy chi nhánh!");
            return "redirect:/admin/branches";
        }
        Page<ScreeningRoomDto> roomPage = screeningRoomService.getRoomsPage(branchId, page, size);
        ScreeningRoomDto screeningRoomDto = new ScreeningRoomDto();
        screeningRoomDto.setBranch(branch);
        screeningRoomDto.setRows(10);
        screeningRoomDto.setSeatsPerRow(12);
        model.addAttribute("branch", branch);
        model.addAttribute("roomPage", roomPage);
        model.addAttribute("screeningRooms", roomPage.getContent());
        model.addAttribute("screeningRoom", screeningRoomDto);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", roomPage.getTotalPages());
        model.addAttribute("pageSize", size);
        return "admin_screening_room_management";
    }
    
    @GetMapping("/add")
    public String addScreeningRoomForm(@org.springframework.web.bind.annotation.RequestParam("branchId") int branchId, Model model) {
        BranchDto branch = branchService.findById(branchId);
        ScreeningRoomDto screeningRoomDto = new ScreeningRoomDto();
        screeningRoomDto.setBranch(branch);
        screeningRoomDto.setRows(8);
        screeningRoomDto.setSeatsPerRow(8);
        screeningRoomDto.setStatus("ACTIVE");
        model.addAttribute("screeningRoom", screeningRoomDto);
        return "admin_screening_room_edit";
    }
    
    @PostMapping("/add")
    public String addScreeningRoom(@ModelAttribute("screeningRoom") ScreeningRoomDto screeningRoomDto, 
                                   RedirectAttributes redirectAttributes, Model model) {
        int rows = screeningRoomDto.getRows();
        int seats = screeningRoomDto.getSeatsPerRow();
        if (rows != seats) {
            redirectAttributes.addFlashAttribute("error", "Chỉ cho phép phòng vuông (AxA)");
            return "redirect:/admin/screening-rooms/branch/" + screeningRoomDto.getBranch().getId();
        } else if (rows % 2 != 0) {
            redirectAttributes.addFlashAttribute("error", "Số hàng và số ghế mỗi hàng phải là số chẵn");
            return "redirect:/admin/screening-rooms/branch/" + screeningRoomDto.getBranch().getId();
        } else if ((rows / 2) % 2 != 0) {
            redirectAttributes.addFlashAttribute("error", "Số hàng (và số ghế mỗi hàng) chia 2 phải là số chẵn (để chia ghế couple)");
            return "redirect:/admin/screening-rooms/branch/" + screeningRoomDto.getBranch().getId();
        }
        try {
            screeningRoomService.saveOrUpdate(screeningRoomDto);
            redirectAttributes.addFlashAttribute("success", "Thêm phòng chiếu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể thêm phòng chiếu: " + e.getMessage());
        }
        return "redirect:/admin/screening-rooms/branch/" + screeningRoomDto.getBranch().getId();
    }
    
    @GetMapping("/edit/{id}")
    public String editScreeningRoomForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        ScreeningRoomDto screeningRoom = screeningRoomService.getRoomById(id);
        if (screeningRoom == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy phòng chiếu!");
            return "redirect:/admin/branches";
        }
        
        model.addAttribute("screeningRoom", screeningRoom);
        return "admin_screening_room_edit";
    }
    
    @PostMapping("/edit/{id}")
    public String editScreeningRoom(@PathVariable int id, @ModelAttribute("screeningRoom") ScreeningRoomDto screeningRoomDto, 
                                    RedirectAttributes redirectAttributes, Model model) {
        int rows = screeningRoomDto.getRows();
        int seats = screeningRoomDto.getSeatsPerRow();
        if (rows != seats) {
            redirectAttributes.addFlashAttribute("error", "Chỉ cho phép phòng vuông (AxA)");
            return "redirect:/admin/screening-rooms/edit/" + id;
        } else if (rows % 2 != 0) {
            redirectAttributes.addFlashAttribute("error", "Số hàng và số ghế mỗi hàng phải là số chẵn");
            return "redirect:/admin/screening-rooms/edit/" + id;
        } else if ((rows / 2) % 2 != 0) {
            redirectAttributes.addFlashAttribute("error", "Số hàng (và số ghế mỗi hàng) chia 2 phải là số chẵn (để chia ghế couple)");
            return "redirect:/admin/screening-rooms/edit/" + id;
        }
        try {
            screeningRoomDto.setId(id);
            screeningRoomService.saveOrUpdate(screeningRoomDto);
            redirectAttributes.addFlashAttribute("success", "Cập nhật phòng chiếu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể cập nhật phòng chiếu: " + e.getMessage());
        }
        return "redirect:/admin/screening-rooms/branch/" + screeningRoomDto.getBranch().getId();
    }
    
    @GetMapping("/delete/{id}")
    public String deleteScreeningRoom(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            ScreeningRoomDto screeningRoom = screeningRoomService.getRoomById(id);
            if (screeningRoom == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy phòng chiếu!");
                return "redirect:/admin/branches";
            }
            
            int branchId = screeningRoom.getBranch().getId();
            screeningRoomService.deleteRoom(id);
            redirectAttributes.addFlashAttribute("success", "Xóa phòng chiếu thành công!");
            return "redirect:/admin/screening-rooms/branch/" + branchId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa phòng chiếu. Phòng có thể đang được sử dụng.");
            return "redirect:/admin/branches";
        }
    }
} 